package tabulate

import java.io.Reader

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

private object CsvParser {
  // Possible reasons for breaking off a cell or row.
  case object Separator extends Break
  case object CR extends Break
  case object LF extends Break
  case object EOF extends Break
  sealed trait Break

  // Possible outcomes of parsing the beginning of a cell.
  case class Finished(reason: Break) extends CellStart
  val CSeparator = Finished(Separator)
  val CCR = Finished(CR)
  val CLF = Finished(LF)
  val CEOF = Finished(EOF)
  case object Escaped extends CellStart
  case object Raw extends CellStart
  sealed trait CellStart
}

private[tabulate] case class CsvParser(data: Reader, separator: Char) extends CsvRows[DecodeResult[Seq[String]]] {
  private val cell = new StringBuilder
  private val row  = ArrayBuffer[String]()

  private var leftover: Char = _
  private var hasLeftover: Boolean = false

  private var mark: Int = 0
  private var index: Int = 0
  private var length: Int = 0
  private val characters: Array[Char] = new Array[Char](2048)

  @inline
  private def dumpCell(): Unit = {
    if(index != mark) cell.appendAll(characters, mark, index - mark - 1)
    ()
  }

  private def endCell(): Unit = {
    if(cell.isEmpty) {
      if(index != mark) row += new String(characters, mark, index - mark - 1)
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

  final def hasNextChar: Boolean = {
    if(length < 0) false
    else if(index < length) true
    else {
      if(index != mark) cell.appendAll(characters, mark, index - mark)
      length = data.read(characters)
      mark   = 0
      index  = 0
      length > 0
    }
  }

  @tailrec
  final def cellStart(c: Char): CsvParser.CellStart = c match {
    // Separator: empty cell, but a next one is coming.
    case `separator` =>
      endCell()
      CsvParser.CSeparator

    // CR: empty cell, end of row.
    case '\r' =>
      endCell()
      CsvParser.CCR

    // LF: empty cell, end of row.
    case '\n'        =>
      endCell()
      CsvParser.CLF

    // ": start of escaped cell.
    case '"'         =>
      mark = index
      CsvParser.Escaped

    // whitespace: unsure, either whitespace before an escaped cell or part of a raw cell.
    case _ if c.isWhitespace =>
      if(hasNextChar) cellStart(nextChar())
      else            CsvParser.CEOF

    // Anything else: raw cell.
    case _ =>
      CsvParser.Raw
  }

  @inline
  final def nextCell(c: Char): CsvParser.Break = cellStart(c) match {
    case CsvParser.Raw         => rawCell
    case CsvParser.Escaped     => escapedCell(false)
    case CsvParser.Finished(r) => r
  }

  @tailrec
  final def rawCell: CsvParser.Break =
    if(hasNextChar) nextChar() match {
      case `separator` =>
        endCell()
        CsvParser.Separator

      case '\r' =>
        endCell()
        CsvParser.CR

      case '\n' =>
        endCell()
        CsvParser.LF

      case c => rawCell
    }
    else {
      endCell()
      CsvParser.EOF
    }

  @tailrec
  final def escapedCellEnd(c: Char): CsvParser.Break = c match {
    case `separator` =>
      endCell()
      CsvParser.Separator

    case '\r' =>
      endCell()
      CsvParser.CR

    case '\n' =>
      endCell()
      CsvParser.LF

    case _ if c.isWhitespace =>
      if(hasNextChar) escapedCellEnd(nextChar())
      else            CsvParser.EOF

    case _                    => sys.error("illegal CSV data")
  }

  @tailrec
  final def escapedCell(prev: Boolean): CsvParser.Break = {
    if(hasNextChar) {
      val c = nextChar()

      if(c == '"') {
        dumpCell()
        mark = index
        if(prev) {
          cell.append('"')
          escapedCell(false)
        }
        else escapedCell(true)
      }

      // End of escaped cell. We might have to skip some whitespace.
      else if(prev) escapedCellEnd(c)

      else escapedCell(false)
    }
    else {
      endCell()
      CsvParser.EOF
    }
  }

  @tailrec
  final def nextRow(c: Char): Unit = {
    nextCell(c) match {
      case CsvParser.Separator =>
        if(hasNextChar) nextRow(nextChar())
        else {
          row += ""
          ()
        }
      case CsvParser.CR if hasNextChar =>
        leftover = nextChar()
        if(leftover == '\n') {
          mark += 1
        }
        else hasLeftover = true

      case _ =>
        hasLeftover = false
        ()
    }
  }

  override def hasNext: Boolean = hasNextChar || hasLeftover
  override protected def readNext(): DecodeResult[Seq[String]] = {
    row.clear()
    if(hasLeftover) nextRow(leftover)
    else if(hasNextChar) nextRow(nextChar())
    else throw new NoSuchElementException
    DecodeResult(row)
  }
  override def close()  = data.close()
}