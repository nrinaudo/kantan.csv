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

import java.io._
import kantan.codecs.resource.WriterResource
import kantan.csv.engine.WriterEngine

/** Type class for all types that can be turned into [[CsvWriter]] instances.
  *
  * Instances of [[CsvSink]] are rarely used directly. The preferred, idiomatic way is to use the implicit syntax
  * provided by [[ops.sink CsvSinkOps]], brought in scope by importing `kantan.csv.ops._`.
  *
  * See the [[CsvSink companion object]] for default implementations and construction methods.
  */
trait CsvSink[-S] extends VersionSpecificCsvSink[S] with Serializable { self =>

  /** Opens a `Writer` on the specified `S`. */
  def open(s: S): Writer

  @deprecated("use writer(S, CsvConfiguration) instead", "0.1.18")
  def writer[A: HeaderEncoder](s: S, sep: Char, header: String*)(implicit e: WriterEngine): CsvWriter[A] =
    writer(s, rfc.withCellSeparator(sep).withHeader(header: _*))

  /** Opens a [[CsvWriter]] on the specified `S`.
    *
    * @param s what to open a [[CsvWriter]] on.
    * @param conf CSV writing behaviour.
    */
  def writer[A: HeaderEncoder](s: S, conf: CsvConfiguration)(implicit e: WriterEngine): CsvWriter[A] =
    CsvWriter(open(s), conf)

  /** Turns a `CsvSink[S]` into a `CsvSink[T]`.
    *
    * This allows developers to adapt existing instances of [[CsvSink]] rather than write one from scratch.
    *
    * One could, for example, write `CsvSource[File]` by basing it on `CsvSource[OutputStream]`:
    * {{{
    *   def fileOutput(implicit c: scala.io.Codec): CsvSink[File] =
    *     CsvSink[OutputStream].contramap(f ⇒ new FileOutputStream(f, c.charSet))
    * }}}
    */
  def contramap[T](f: T => S): CsvSink[T] = CsvSink.from(f andThen self.open)
}

/** Provides default instances as well as instance summoning and creation methods. */
object CsvSink {

  /** Summons an implicit instance of `CsvSink[A]` if one can be found.
    *
    * This is simply a convenience method. The two following calls are equivalent:
    * {{{
    *   val file: CsvSink[File] = CsvSink[File]
    *   val file2: CsvSink[File] = implicitly[CsvSink[File]]
    * }}}
    */
  def apply[A](implicit ev: CsvSink[A]): CsvSink[A] = macro imp.summon[CsvSink[A]]

  /** Turns the specified function into a [[CsvSink]].
    *
    * Note that it's usually better to compose an existing instance through [[CsvSink.contramap]] rather than create
    * one from scratch.
    */
  def from[A](f: A => Writer): CsvSink[A] = new CsvSink[A] {
    override def open(s: A): Writer = f(s)
  }

  // TODO: unsafe, unacceptable, what was I thinking.
  @SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
  implicit def fromResource[A: WriterResource]: CsvSink[A] =
    CsvSink.from(
      a =>
        WriterResource[A]
          .open(a)
          .fold(
            error => sys.error(s"Failed to open resource $a: $error"),
            w => w
          )
    )
}
