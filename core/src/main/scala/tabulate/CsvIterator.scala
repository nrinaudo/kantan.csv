package tabulate

import java.io.{Closeable, IOException}

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/** Defines the various possible states of a CSV parser. */
private object CsvIterator {
  object Status {
    case object Normal extends Status
    case object Escaping extends Status
    case object LeavingEscape extends Status
  }
  sealed trait Status
}

private[tabulate] class CsvIterator(data: Source, separator: Char)
  extends Iterator[DecodeResult[ArrayBuffer[String]]] with Closeable {
  import CsvIterator._

  /** Used to aggregate the content of the current cell. */
  private val cell = new StringBuilder
  /** Used to aggregate the content of the current row. */
  private val row = ArrayBuffer[String]()
  /** Parser status. */
  private var status: Status = Status.Normal
  /** Iterator on the CSV data. We need this to be buffered to deal with possible `\r\n` sequences. */
  private val input: BufferedIterator[Char] = data.buffered
  /** Number of whitespace found at the end of and escaped cell. */
  private var wCount = 0
  private var line = 0
  private var column = 0

  /** Appends the content of current cell to the current row. */
  private def appendCell() = {
    row += cell.result()
    cell.clear()
    row
  }

  /** Checks whether the specified character is a line break.
    *
    * Note that this might consume a character from the input stream if `c` is a line feed and the next character is a
    * line break.
    */
  private def isLineBreak(c: Char): Boolean = {
    def resetLine(): Boolean = {
      line  += 1
      column = 0
      true
    }

    if(c == '\n') resetLine()
    else if(c == '\r') {
      if(input.hasNext && input.head == '\n') input.next()
      resetLine()
    }
    else {
      column += 1
      false
    }
  }

  /** Attempts to read and interpret the next character in the stream.
    *
    * @return `false` if the stream is empty, `true` otherwise.
    */
  private def parseNext(): Boolean = if(input.hasNext) {
    val c = input.next()

    status match {
      // - Normal status -----------------------------------------------------------------------------------------------
      // ---------------------------------------------------------------------------------------------------------------
      case Status.Normal =>
        if(isLineBreak(c)) {
          appendCell()
          false
        }

        else {
          // Separator character: we've found a new cell in the current row.
          if(c == separator) appendCell()

          // Escape character: if at the beginning of the cell, marks it as an escaped cell. Otherwise, treats it as
          // a normal character.
          else if(c == '"') {
            if(cell.isEmpty) status = Status.Escaping
            else cell += c
          }

          // Regular character, appended to the current cell.
          else cell += c
          true
        }



      // - Within escaped content --------------------------------------------------------------------------------------
      // ---------------------------------------------------------------------------------------------------------------
      case Status.Escaping =>
        if(c == '"')            status = Status.LeavingEscape
        else if(isLineBreak(c)) cell += '\n'
        else                    cell += c

        true



      // - Ending escape mode ------------------------------------------------------------------------------------------
      // ---------------------------------------------------------------------------------------------------------------
      case Status.LeavingEscape =>
        if(isLineBreak(c)) {
          appendCell()
          wCount = 0
          status = Status.Normal
          false
        }
        // This means that 2 " characters were found in escape mode: that's an escaped ".
        else {
          if(c == '"' && wCount == 0) {
            cell += '"'
            wCount = 0
            status = Status.Escaping
          }

          // End of escaped cell.
          else if(c == separator) {
            appendCell()
            wCount = 0
            status = Status.Normal
          }
          else if(c.isWhitespace) wCount += 1
          else throw new IOException(s"Illegal CSV format: unexpected character '$c'")
          true
        }
    }
  }
  else if(status == Status.Escaping) throw new IOException("Illegal CSV format: non-terminated escape sequence")
  else {
    if(cell.nonEmpty || row.nonEmpty) appendCell()
    false
  }

  override def hasNext: Boolean = input.hasNext
  override def next(): DecodeResult[ArrayBuffer[String]] = {
    try {
      row.clear()

      while(parseNext()) {}

      // If we've finished parsing the whole stream, close it.
      if(!hasNext) close()

      DecodeResult.success(row)
    }
    catch {
      case _: Exception =>
        // Closes the underlying stream, ignores errors at this point.
        try { close() }
        catch { case _: Exception => }

        DecodeResult.readFailure(line, column)
    }
  }

  override def close(): Unit = data.close()
}