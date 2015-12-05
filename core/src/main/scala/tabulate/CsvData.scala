package tabulate

import java.io.{Closeable, InputStream, Reader}

import scala.io.{Codec, Source}

/** Declares helper methods from creating instances of [[CsvData]]. */
object CsvData {
  /** Turns a `scala.io.Source` into an instance of [[tabulate.CsvData]]. */
  def apply(s: Source): CsvData = new CsvData {
    override def close() = s.close()
    override def next() = s.next()
    override def hasNext = s.hasNext
  }

  /** Turns an `java.io.InputStream` into an instance of [[tabulate.CsvData]]. */
  def apply[I <: InputStream](i: I)(implicit codec: Codec): CsvData = CsvData(Source.fromInputStream(i))

  /** Turns a `java.io.Reader` into an instance of [[tabulate.CsvData]]. */
  def apply[R <: Reader](r: R): CsvData = new CsvData {
    private var n = r.read()

    override def close() = r.close()

    override def next() = {
      val c = n.toChar
      n = r.read()
      c
    }
    override def hasNext = n != -1
  }
}

/** Represents an iterator on CSV data.
  *
  * Creation methods can be found in the [[CsvData$ companion object]].
  */
trait CsvData extends Iterator[Char] with Closeable {
  /** Turns this instance into a [[CsvRows]]`[A]`, provided `A` has a valid [[RowDecoder]] instance in scope. */
  def asRows[A](separator: Char, header: Boolean)(implicit da: RowDecoder[A]): CsvRows[DecodeResult[A]] = {
    val data = CsvRows(this, separator)

    if(header && data.hasNext) data.next()
    data.map(r => r.flatMap(da.decode))
  }
}
