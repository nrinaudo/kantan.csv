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

import java.io.{Closeable, Writer}
import kantan.csv.engine._

/** Type of values that know how to write CSV data.
  *
  * There should almost never be a reason to implement this trait directly. The default implementation should satisfy
  * most needs, and others can be swapped if needed through the [[kantan.csv.engine.WriterEngine]] mechanism.
  *
  * @tparam A type of values that will be encoded as CSV.
  */
trait CsvWriter[A] extends VersionSpecificCsvWriter[A] with Closeable { self =>

  /** Encodes and writes a single `A`. */
  def write(a: A): CsvWriter[A]

  /** Releases the underlying resource.
    *
    * Calling this method when there is no more data to write is critical. Not doing so might result in a cached
    * resource not flushing its buffers, for example, and the resulting CSV data not being complete or even valid.
    */
  override def close(): Unit

  /** Turns a `CsvWriter[A]` into a `CsvWriter[B]`. */
  def contramap[B](f: B => A): CsvWriter[B] = new CsvWriter[B] {
    override def write(b: B): CsvWriter[B] = {
      self.write(f(b))
      this
    }
    override def close(): Unit = self.close()
  }
}

/** Provides useful instance creation methods. */
object CsvWriter {
  @deprecated("use apply(writer, CsvConfiguration) instead", "0.1.18")
  def apply[A: HeaderEncoder](writer: Writer, sep: Char, header: String*)(implicit engine: WriterEngine): CsvWriter[A] =
    CsvWriter(writer, rfc.withCellSeparator(sep).withHeader(header: _*))

  /** Creates a new [[CsvWriter]] instance that will send encoded data to the specified `Writer`.
    *
    * Which implementation of [[CsvWriter]] is returned is controlled by whatever implicit
    * [[kantan.csv.engine.WriterEngine]] is found in scope. If none is explicitly imported, the
    * [[kantan.csv.engine.WriterEngine$.internalCsvWriterEngine internal]] one will be used.
    *
    * @param writer where to write CSV data to.
    * @param conf CSV writing behaviour.
    * @tparam A type of values that the returned instance will know to encode.
    */
  def apply[A: HeaderEncoder](writer: Writer, conf: CsvConfiguration)(implicit engine: WriterEngine): CsvWriter[A] = {
    val w = engine.writerFor(writer, conf)
    conf.header match {
      case CsvConfiguration.Header.Implicit =>
        HeaderEncoder[A].header.foreach(w.write)
      case CsvConfiguration.Header.Explicit(seq) =>
        w.write(seq)
      case CsvConfiguration.Header.None => ()
    }
    w.contramap(HeaderEncoder[A].rowEncoder.encode)
  }

  /** Creates a new [[CsvWriter]] instance.
    *
    * This method is meant to help interface third party libraries with kantan.csv.
    *
    * @param out where to send CSV rows to - this is meant to be a third party library's csv writer.
    * @param w writes a CSV row using `out`.
    * @param r releases `out` once we're done writing.
    */
  def apply[A](out: A)(w: (A, Seq[String]) => Unit)(r: A => Unit): CsvWriter[Seq[String]] = new CsvWriter[Seq[String]] {
    override def write(a: Seq[String]): CsvWriter[Seq[String]] = {
      w(out, a)
      this
    }
    override def close(): Unit = r(out)
  }
}
