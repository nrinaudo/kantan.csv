package tabulate

import java.io.{Closeable, InputStream, Reader}

import scala.io.{Codec, Source}

object CsvData {
  /** Turns a source into an instance of [[tabulate.CsvData]]. */
  def apply(s: Source): CsvData = new CsvData {
    override def close(): Unit = s.close()
    override def next(): Char = s.next()
    override def hasNext: Boolean = s.hasNext
  }

  /** Turns an input stream into an instance of [[tabulate.CsvData]]. */
  def apply[I <: InputStream](i: I)(implicit codec: Codec): CsvData = CsvData(Source.fromInputStream(i))

  /** Turns a reader into an instance of [[tabulate.CsvData]]. */
  def apply[R <: Reader](r: R): CsvData = new CsvData {
    private var n = r.read()

    override def close(): Unit = r.close()

    override def next(): Char = {
      val c = n.toChar
      n = r.read()
      c
    }
    override def hasNext: Boolean = n != -1
  }
}

trait CsvData extends Iterator[Char] with Closeable {
  def asRows[A](separator: Char, header: Boolean)(implicit da: RowDecoder[A]): CsvRows[DecodeResult[A]] = {
    val data = CsvRows(this, separator)

    if(header && data.hasNext) data.next()
    data.map(r => r.flatMap(da.decode))
  }
}
