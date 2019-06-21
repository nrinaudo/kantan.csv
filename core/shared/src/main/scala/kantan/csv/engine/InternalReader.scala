/*
 * Copyright 2015 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.csv
package engine

import java.io.Reader
import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

@SuppressWarnings(Array("org.wartremover.warts.MutableDataStructures", "org.wartremover.warts.Var"))
private[engine] class InternalReader private (
  val data: Reader,
  val conf: CsvConfiguration,
  val characters: Array[Char],
  var length: Int
) extends CsvReader[Seq[String]] {
  private val cell = new StringBuilder
  private val row  = ArrayBuffer[String]()

  private var leftover: Char       = _
  private var hasLeftover: Boolean = false

  private var mark: Int  = 0
  private var index: Int = 0

  @inline private def dumpCell(): Unit = {
    if(index != mark) cell.appendAll(characters, mark, index - mark - 1)
    ()
  }

  private def endCell(): Unit = {
    if(cell.isEmpty) {
      if(index != mark) row += new String(characters, mark, index - mark - 1)
      else row              += ""
    }
    else {
      dumpCell()
      row += cell.toString()
      cell.clear()
    }
    mark = index
  }

  def nextChar(): Char = {
    val c = characters(index)
    index += 1
    c
  }

  final def hasNextChar: Boolean =
    if(length < 0) false
    else if(index < length) true
    else {
      if(index != mark) cell.appendAll(characters, mark, index - mark)
      length = data.read(characters)
      mark = 0
      index = 0
      length > 0
    }

  @tailrec
  final def cellStart(c: Char): InternalReader.CellStart = c match {
    // Separator: empty cell, but a next one is coming.
    case conf.cellSeparator =>
      endCell()
      InternalReader.CSeparator

    // CR: empty cell, end of row.
    case '\r' =>
      endCell()
      InternalReader.CCR

    // LF: empty cell, end of row.
    case '\n' =>
      endCell()
      InternalReader.CLF

    // ": start of escaped cell.
    case conf.quote =>
      mark = index
      InternalReader.Escaped

    // whitespace: unsure, either whitespace before an escaped cell or part of a raw cell.
    case _ if c.isWhitespace =>
      if(hasNextChar) cellStart(nextChar())
      else {
        endCell()
        InternalReader.CEOF
      }

    // Anything else: raw cell.
    case _ =>
      InternalReader.Raw
  }

  @inline
  final def nextCell(c: Char): InternalReader.Break = cellStart(c) match {
    case InternalReader.Raw         => rawCell
    case InternalReader.Escaped     => escapedCell(false)
    case InternalReader.Finished(r) => r
  }

  @tailrec
  final def rawCell: InternalReader.Break =
    if(hasNextChar) nextChar() match {
      // Separator: cell finished.
      case conf.cellSeparator =>
        endCell()
        InternalReader.Separator

      // CR: cell finished, we need to check for a trailing \n
      case '\r' =>
        endCell()
        InternalReader.CR

      // LF: cell finished.
      case '\n' =>
        endCell()
        InternalReader.LF

      // Anything else: part of the cell.
      case _ => rawCell
    }
    // EOF: cell finished.
    else {
      endCell()
      InternalReader.EOF
    }

  @tailrec
  final def escapedCellEnd(c: Char): InternalReader.Break = c match {
    case conf.cellSeparator =>
      endCell()
      InternalReader.Separator

    case '\r' =>
      endCell()
      InternalReader.CR

    case '\n' =>
      endCell()
      InternalReader.LF

    case _ if c.isWhitespace =>
      if(hasNextChar) escapedCellEnd(nextChar())
      else {
        endCell()
        InternalReader.EOF
      }

    case conf.quote => escapedCell(true)

    case _ =>
      cell.append(conf.quote)
      escapedCell(false)
  }

  @tailrec
  final def escapedCell(prev: Boolean): InternalReader.Break =
    if(hasNextChar) {
      val c = nextChar()

      if(c == conf.quote) {
        dumpCell()
        mark = index
        if(prev) {
          cell.append(conf.quote)
          escapedCell(false)
        }
        else escapedCell(true)
      }
      else if(prev) escapedCellEnd(c)
      else escapedCell(false)
    }
    else {
      endCell()
      InternalReader.EOF
    }

  @tailrec
  final def nextRow(c: Char): Unit = nextCell(c) match {
    // Cell finished, row not done.
    case InternalReader.Separator =>
      if(hasNextChar) nextRow(nextChar())
      // The next cell is empty AND there is no further data to read.
      else {
        row += ""
        ()
      }

    // Row finished, might have a trailing LF.
    case InternalReader.CR if hasNextChar =>
      leftover = nextChar()

      // The leftover char is a LF: skip it.
      if(leftover == '\n') {
        hasLeftover = false
        mark += 1
      }
      else hasLeftover = true

    // Row finished, no trailing LF.
    case _ =>
      hasLeftover = false
      ()
  }

  override def checkNext = hasNextChar || hasLeftover

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  override def readNext() = {
    row.clear()
    if(hasLeftover) nextRow(leftover)
    else if(hasNextChar) nextRow(nextChar())
    else throw new NoSuchElementException

    hasNextChar
    row.toSeq
  }
  override def release() = data.close()
}

private object InternalReader {
  def apply(data: Reader, conf: CsvConfiguration): InternalReader = {
    val characters = new Array[Char](2048)
    val length     = data.read(characters)

    new InternalReader(data, conf, characters, length)
  }

  // Possible reasons for breaking off a cell or row.
  case object Separator extends Break
  case object CR        extends Break
  case object LF        extends Break
  case object EOF       extends Break
  sealed trait Break

  // Possible outcomes of parsing the beginning of a cell.
  final case class Finished(reason: Break) extends CellStart
  val CSeparator = Finished(Separator)
  val CCR        = Finished(CR)
  val CLF        = Finished(LF)
  val CEOF       = Finished(EOF)
  case object Escaped extends CellStart
  case object Raw     extends CellStart
  sealed trait CellStart
}
