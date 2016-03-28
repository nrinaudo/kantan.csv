package kantan.csv

import java.io.{Closeable, Writer}
import kantan.csv.engine._

/** Type of values that know how to write CSV data.
  *
  * There should almost never be a reason to implement this trait directly. The default implementation should satisfy
  * most needs, and others can be swapped if needed through the [[kantan.csv.engine.WriterEngine]] mechanism.
  *
  * @tparam A type of values that will be encoded as CSV.
  */
trait CsvWriter[A] extends Closeable { self ⇒
  /** Encodes and writes a single `A`. */
  def write(a: A): CsvWriter[A]

  /** Encodes and writes a collection of `A`s. */
  def write(as: TraversableOnce[A]): CsvWriter[A] = {
    for(a ← as) write(a)
    this
  }

  /** Releases the underlying resource.
    *
    * Calling this method when there is no more data to write is critical. Not doing so might result in a cached
    * resource not flushing its buffers, for example, and the resulting CSV data not being complete or even valid.
    */
  override def close(): Unit

  /** Turns a `CsvWriter[A]` into a `CsvWriter[B]`. */
  def contramap[B](f: B ⇒ A): CsvWriter[B] = new CsvWriter[B] {
    override def write(b: B): CsvWriter[B] = {
      self.write(f(b))
      this
    }
    override def close(): Unit = self.close()
  }
}

/** Provides useful instance creation methods. */
object CsvWriter {
  /** Creates a new [[CsvWriter]] instance that will send encoded data to the specified `Writer`.
    *
    * Which implementation of [[CsvWriter]] is returned is controlled by whatever implicit
    * [[kantan.csv.engine.WriterEngine]] is found in scope. If none is explicitly imported, the
    * [[kantan.csv.engine.WriterEngine$.internal internal]] one will be used.
    *
    * @param writer where to write CSV data to.
    * @param separator column separator.
    * @param header optional header row, defaults to none.
    * @tparam A type of values that the returned instance will know to encode.
    */
  def apply[A](writer: Writer, separator: Char, header: Seq[String] = Seq.empty)(implicit ea: RowEncoder[A], engine: WriterEngine): CsvWriter[A] = {
    if(header.isEmpty) engine.writerFor(writer, separator).contramap(ea.encode)
    else {
      val w = engine.writerFor(writer, separator)
      w.write(header)
      w.contramap(ea.encode)
    }
  }

  def apply[A](out: A)(w: (A, Seq[String]) ⇒ Unit)(r: A ⇒ Unit): CsvWriter[Seq[String]] = new CsvWriter[Seq[String]] {
    override def write(a: Seq[String]) = {
      w(out, a)
      this
    }
    override def close() = r(out)
  }
}