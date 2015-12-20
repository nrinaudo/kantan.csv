package tabulate

import java.io.Closeable

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

object CsvRows {
  def apply(data: CsvData, separator: Char): CsvRows[DecodeResult[Seq[String]]] = DataRows(data, separator)

  val empty: CsvRows[Nothing] = new CsvRows[Nothing] {
    override def readNext() = throw new NoSuchElementException("next on empty CSV rows")
    override def hasNext = false
    override def close() = {}
  }
}

/** Mutable collection of CSV rows.
  *
  * This class is very similar to an `Iterator`, with an added [[close]] method to close the underlying source of CSV
  * data.
  */
trait CsvRows[+A] extends TraversableOnce[A] with Closeable { self =>
  def hasNext: Boolean
  protected def readNext(): A
  def close(): Unit

  def next(): A = {
    val a = {
      try { readNext() }
      catch {
        case e: Throwable =>
          close()
          throw e
      }
    }
    if(!hasNext) close()
    a
  }


  // - Useful methods --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def drop(n: Int): CsvRows[A] =
    if(n > 0 && hasNext) {
      next()
      drop(n - 1)
    }
    else this

  def dropWhile(p: A => Boolean): CsvRows[A] =
  // Empty rows: nothing to drop
    if(isEmpty) this
    else {
      // Looks for the first element that does not match p.
      var n = self.next()
      while(self.hasNext && p(n)) n = self.next()

      // No such element, return the empty stream.
      if(isEmpty && p(n)) this

      // We've found one such element, returns a bit of a mess of a CsvRows that'll first return it, then whatever is
      // left in the stream.
      else new CsvRows[A] {
        var done = false

        override def hasNext: Boolean = !done || self.hasNext
        override def close() = self.close()

        override def readNext(): A =
          if(done) self.readNext()
          else {
            done = true
            n
          }
      }
    }

  def take(n: Int): CsvRows[A] = new CsvRows[A] {
    var count = n
    override def hasNext: Boolean = count > 0 && self.hasNext
    override def readNext(): A = {
      if(count > 0) {
        val a = self.readNext()
        count -= 1
        a
      }
      else CsvRows.empty.next()
    }
    override def close(): Unit = self.close()
  }


  // - Monadic operations ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def map[B](f: A => B): CsvRows[B] = new CsvRows[B] {
    override def hasNext: Boolean = self.hasNext
    override def readNext(): B = f(self.readNext())
    override def close(): Unit = self.close()
  }

  def flatMap[B](f: A => CsvRows[B]): CsvRows[B] = new CsvRows[B] {
    private var cur: CsvRows[B] = CsvRows.empty

    override def hasNext: Boolean = cur.hasNext || self.hasNext && { cur = f(self.next()); hasNext}
    override def readNext(): B = cur.readNext()
    override def close(): Unit = self.close()
  }

  def filter(p: A => Boolean): CsvRows[A] = new CsvRows[A] {
    var n = self.find(p)
    override def hasNext: Boolean = n.isDefined
    override def readNext(): A = {
      val r = n.getOrElse(CsvRows.empty.readNext())
      n = self.find(p)
      r
    }
    override def close(): Unit = self.close()
  }

  def withFilter(p: A => Boolean): CsvRows[A] = filter(p)


  // - TraversableOnce -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  override def foreach[U](f: A => U): Unit = while(hasNext) f(next())
  override def seq: TraversableOnce[A] = this
  override def hasDefiniteSize: Boolean = isEmpty

  override def copyToArray[B >: A](xs: Array[B], start: Int, len: Int): Unit = {
    var i = start
    val end = start + math.min(len, xs.length - start)
    while(i < end && hasNext) {
      xs(i) = next()
      i += 1
    }
  }

  override def forall(p: A => Boolean): Boolean = {
    var res = true
    while(res && hasNext) res = p(next())
    res
  }
  override def toTraversable: Traversable[A] = toStream
  override def isEmpty: Boolean = !hasNext
  override def find(p: A => Boolean): Option[A] = {
    var res: Option[A] = None
    while(res.isEmpty && hasNext) {
      val n = next()
      if(p(n)) res = Some(n)
    }
    res
  }
  override def exists(p: A => Boolean): Boolean = {
    var res = false
    while(!res && hasNext) res = p(next())
    res
  }
  override def toStream: Stream[A] = if(hasNext) Stream.cons(next(), toStream) else Stream.empty
  override def toIterator: Iterator[A] = new Iterator[A] {
    override def hasNext: Boolean = self.hasNext
    override def next(): A = self.next()
  }
  override def isTraversableAgain: Boolean = false
}

private object DataRows {
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

private case class DataRows(data: CsvData, separator: Char) extends CsvRows[DecodeResult[Seq[String]]] {
  private val cell = new StringBuilder
  private val row  = ArrayBuffer[String]()
  var buffer: Char = _
  var leftover = false

  @tailrec
  final def cellStart(c: Char): DataRows.CellStart = c match {
    case `separator` => DataRows.CSeparator
    case '\r'        => DataRows.CCR
    case '\n'        => DataRows.CLF
    case '"'         =>
      cell.clear()
      DataRows.Escaped
    case _ =>
      cell.append(c)
      if(data.hasNext) {
        if(c.isWhitespace) cellStart(data.next())
        else DataRows.Raw
      }
      else DataRows.CEOF
  }

  @inline
  final def nextCell(c: Char): DataRows.Break = cellStart(c) match {
    case DataRows.Raw         => rawCell
    case DataRows.Escaped     => escapedCell(false)
    case DataRows.Finished(r) => r
  }

  @tailrec
  final def escapedCellEnd(c: Char): DataRows.Break = c match {
    case `separator`         => DataRows.Separator
    case '\r'                => DataRows.CR
    case '\n'                => DataRows.LF
    case _ if c.isWhitespace =>
      if(data.hasNext) escapedCellEnd(data.next())
      else             DataRows.EOF
    case _                    => sys.error("illegal CSV data")
  }

  @tailrec
  final def escapedCell(prev: Boolean): DataRows.Break = {
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
    else DataRows.EOF
  }

  @tailrec
  final def rawCell: DataRows.Break =
    if(data.hasNext) data.next() match {
      case `separator` => DataRows.Separator
      case '\r'        => DataRows.CR
      case '\n'        => DataRows.LF
      case c           =>
        cell.append(c)
        rawCell
    }
    else DataRows.EOF

  final def nextRow(): Unit = {
    var n = {
      if(leftover)          nextCell(buffer)
      else if(data.hasNext) nextCell(data.next())
      else                  DataRows.EOF
    }

    while(n == DataRows.Separator) {
      row += cell.result()
      cell.clear()
      n = if(data.hasNext) nextCell(data.next()) else DataRows.EOF
    }

    row += cell.result()
    cell.clear()

    if(n == DataRows.CR && data.hasNext) {
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