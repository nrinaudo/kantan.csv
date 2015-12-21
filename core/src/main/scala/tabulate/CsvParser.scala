package tabulate

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

private[tabulate] case class CsvParser(data: CsvData, separator: Char) extends CsvRows[DecodeResult[Seq[String]]] {
  private val cell = new StringBuilder
  private val row  = ArrayBuffer[String]()
  var buffer: Char = _
  var leftover = false

  @tailrec
  final def cellStart(c: Char): CsvParser.CellStart = c match {
    case `separator` => CsvParser.CSeparator
    case '\r'        => CsvParser.CCR
    case '\n'        => CsvParser.CLF
    case '"'         =>
      cell.clear()
      CsvParser.Escaped
    case _ =>
      cell.append(c)
      if(data.hasNext) {
        if(c.isWhitespace) cellStart(data.next())
        else CsvParser.Raw
      }
      else CsvParser.CEOF
  }

  @inline
  final def nextCell(c: Char): CsvParser.Break = cellStart(c) match {
    case CsvParser.Raw         => rawCell
    case CsvParser.Escaped     => escapedCell(false)
    case CsvParser.Finished(r) => r
  }

  @tailrec
  final def escapedCellEnd(c: Char): CsvParser.Break = c match {
    case `separator`         => CsvParser.Separator
    case '\r'                => CsvParser.CR
    case '\n'                => CsvParser.LF
    case _ if c.isWhitespace =>
      if(data.hasNext) escapedCellEnd(data.next())
      else             CsvParser.EOF
    case _                    => sys.error("illegal CSV data")
  }

  @tailrec
  final def escapedCell(prev: Boolean): CsvParser.Break = {
    if(data.hasNext) {
      val c = data.next()

      if(c == '"') {
        if(prev) {
          cell.append('"')
          escapedCell(false)
        }
        else escapedCell(true)
      }

      // End of escaped cell. We might have to skip some whitespace.
      else if(prev) escapedCellEnd(c)
      else {
        cell.append(c)
        escapedCell(false)
      }
    }
    else CsvParser.EOF
  }

  @tailrec
  final def rawCell: CsvParser.Break =
    if(data.hasNext) data.next() match {
      case `separator` => CsvParser.Separator
      case '\r'        => CsvParser.CR
      case '\n'        => CsvParser.LF
      case c           =>
        cell.append(c)
        rawCell
    }
    else CsvParser.EOF

  final def nextRow(): Unit = {
    var n = {
      if(leftover)          nextCell(buffer)
      else if(data.hasNext) nextCell(data.next())
      else                  CsvParser.EOF
    }

    while(n == CsvParser.Separator) {
      row += cell.result()
      cell.clear()
      n = if(data.hasNext) nextCell(data.next()) else CsvParser.EOF
    }

    row += cell.result()
    cell.clear()

    if(n == CsvParser.CR && data.hasNext) {
      buffer   = data.next()
      leftover = buffer != '\n'
    }
  }

  override def hasNext: Boolean = data.hasNext
  override protected def readNext(): DecodeResult[Seq[String]] = {
    row.clear()
    nextRow()
    DecodeResult(row)
  }
  override def close()  = data.close()
}