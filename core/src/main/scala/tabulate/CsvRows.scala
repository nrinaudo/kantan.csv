package tabulate

import java.io.{IOException, Closeable}

import scala.collection.mutable.ArrayBuffer

object CsvRows {
  def apply(data: CsvData, separator: Char): CsvRows[DecodeResult[Seq[String]]] = new DataRows(data, separator)

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
  object Status {
    case object Normal extends Status
    case object Escaping extends Status
    case object LeavingEscape extends Status
  }
  sealed trait Status
}

private class DataRows(val data: CsvData, separator: Char) extends CsvRows[DecodeResult[Seq[String]]] {
  import DataRows._

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
  override def readNext(): DecodeResult[Seq[String]] = {
    try {
      row.clear()

      while(parseNext()) {}

      DecodeResult.success(row)
    }
    catch {
      case _: Exception => DecodeResult.readFailure(line, column)
    }
  }

  override def close(): Unit = data.close()
}