/*
 * Copyright 2016 Nicolas Rinaudo
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
import kantan.codecs.Result
import kantan.codecs.resource.{ReaderResource, ResourceIterator}
import kantan.csv.DecodeError.{OutOfBounds, TypeError}
import kantan.csv.ParseError.{IOError, NoSuchElement}
import kantan.csv.engine.ReaderEngine
import scala.collection.generic.CanBuildFrom

/** Turns instances of `S` into valid sources of CSV data.
  *
  * Instances of [[CsvSource]] are rarely used directly. The preferred, idiomatic way is to use the implicit syntax
  * provided by [[ops.CsvSourceOps CsvSourceOps]], brought in scope by importing `kantan.csv.ops._`.
  *
  * See the [[CsvSource$ companion object]] for default implementations and construction methods.
  */
trait CsvSource[-S] extends Serializable { self ⇒
  /** Turns the specified `S` into a `Reader`.
    *
    * Implementations of this method *must* be safe: all non-fatal exceptions should be caught and wrapped in an
    * [[ParseError.IOError]]. This is easily achieved by wrapping unsafe code in a call to [[ParseResult.apply]].
    *
    * @param s instance of `S` to turn into a [[CsvSource]].
    */
  def open(s: S): ParseResult[Reader]

  /** Turns the specified `S` into an iterator on `ReadResult[A]`.
    *
    * This method is "safe", in that it does not throw exceptions when errors are encountered. This comes with the small
    * cost of having each row wrapped in a [[ReadResult]] that then need to be unpacked. See [[unsafeReader]] for an
    * alternative.
    *
    * Using common combinators such as `map`, `flatMap` and `filter` on a `CsvReader[ReadResult[A]]` can be awkward -
    * one needs to first map into the reader, then into the result. For this reason, instances of
    * `CsvReader[ReadResult[A]]` have dedicated syntax that makes it more pleasant through [[ops.CsvReaderOps]].
    *
    * @param s instance of `S` that will be opened an parsed.
    * @param sep character used to separate columns.
    * @param header whether or not the first row is a header. If set to `true`, the first row will be skipped entirely.
    * @tparam A type to parse each row as. This must have a corresponding implicit [[RowDecoder]] instance in scope.
    */
  def reader[A: RowDecoder](s: S, sep: Char, header: Boolean)(implicit e: ReaderEngine): CsvReader[ReadResult[A]] =
    open(s).map(reader ⇒ CsvReader(reader, sep, header))
      .valueOr(error ⇒ ResourceIterator(Result.failure(error)))

  /** Turns the specified `S` into an iterator on `A`.
    *
    * This is the "unsafe" version of [[reader]]: it will throw as soon as an error is encountered.
    *
    * @param s instance of `S` that will be opened an parsed.
    * @param separator character used to separate columns.
    * @param header whether or not the first row is a header. If set to `true`, the first row will be skipped entirely.
    * @tparam A type to parse each row as. This must have a corresponding implicit [[RowDecoder]] instance in scope.
    */
  def unsafeReader[A: RowDecoder](s: S, separator: Char, header: Boolean)(implicit engine: ReaderEngine): CsvReader[A] =
    reader[A](s, separator, header).map(_.valueOr {
      case e@TypeError(msg)   ⇒ throw Option(e.getCause).getOrElse(new IllegalArgumentException(msg))
      case NoSuchElement()    ⇒ throw new NoSuchElementException
      case e@IOError(msg)     ⇒ throw Option(e.getCause).getOrElse(new IOException(msg))
      case OutOfBounds(index) ⇒ throw new ArrayIndexOutOfBoundsException(index)
    })

  /** Reads the entire CSV data into a collection.
    *
    * This method is "safe", in that it does not throw exceptions when errors are encountered. This comes with the small
    * cost of having each row wrapped in a [[ReadResult]] that then need to be unpacked. See [[unsafeRead]] for an
    * alternative.
    *
    * @param s instance of `S` that will be opened an parsed.
    * @param sep character used to separate columns.
    * @param header whether or not the first row is a header. If set to `true`, the first row will be skipped entirely.
    * @tparam C collection type in which to parse the specified `S`.
    * @tparam A type in which to parse each row.
    */
  def read[C[_], A: RowDecoder](s: S, sep: Char, header: Boolean)
                               (implicit e: ReaderEngine,
                                cbf: CanBuildFrom[Nothing, ReadResult[A], C[ReadResult[A]]]): C[ReadResult[A]] =
    reader(s, sep, header).to[C]

  /** Reads the entire CSV data into a collection.
    *
    * This is the "unsafe" version of [[read]]: it will throw as soon as an error is encountered.
    *
    * @param s instance of `S` that will be opened an parsed.
    * @param separator character used to separate columns.
    * @param header whether or not the first row is a header. If set to `true`, the first row will be skipped entirely.
    * @tparam C collection type in which to parse the specified `S`.
    * @tparam A type in which to parse each row.
    */
  def unsafeRead[C[_], A: RowDecoder](s: S, separator: Char, header: Boolean)
                                     (implicit e: ReaderEngine, cbf: CanBuildFrom[Nothing, A, C[A]]): C[A] =
    unsafeReader(s, separator, header).to[C]


  /** Turns an instance of `CsvSource[S]` into one of `CsvSource[T]`.
    *
    * This allows developers to adapt existing instances of [[CsvSource]] rather than write one from scratch.
    * One could, for example, write `CsvSource[String]` by basing it on `CsvSource[Reader]`:
    * {{{
    *   val urlInput: CsvSource[String] = CsvSource[Reader].contramap((s: String) ⇒ new java.io.StringReader(s))
    * }}}
    *
    * Note that this method assumes that the transformation from `T` to `S` is safe. If it fail, one should use
    * [[contramapResult]] instead.
    *
    * @see [[contramapResult]]
    */
  def contramap[T](f: T ⇒ S): CsvSource[T] = CsvSource.from(f andThen self.open)

  /** Turns an instance of `CsvSource[S]` into one of `CsvSource[T]`.
    *
    * This allows developers to adapt existing instances of [[CsvSource]] rather than write one from scratch.
    * One could, for example, write `CsvSource[URL]` by basing it on `CsvSource[InputStream]`:
    * {{{
    *   val urlInput: CsvSource[URL] = CsvSource[InputStream].contramap((url: URL) ⇒ url.openStream())
    * }}}
    *
    * Note that if the transformation from `T` to `S` is safe, it's better to use [[contramap]] and bypass the error
    * handling mechanism altogether.
    *
    * @see [[contramap]]
    */
  def contramapResult[SS <: S, T](f: T ⇒ ParseResult[SS]): CsvSource[T] = CsvSource.from(t ⇒ f(t).flatMap(self.open))
}

trait LowPriorityCsvSourceInstances

/** Defines convenience methods for creating and retrieving instances of [[CsvSource]].
  *
  * Implicit default implementations of standard types are also declared here, always bringing them in scope with a low
  * priority.
  *
  * These default implementations can also be useful when writing more complex instances: if you need to write a
  * `CsvSource[T]` and have both a `CsvSource[S]` and a `T ⇒ S`, you need just use [[CsvSource.contramap]] to create
  * your implementation.
  */
object CsvSource extends LowPriorityCsvSourceInstances {
  /** Summons an implicit instance of `CsvSource[A]` if one can be found.
    *
    * This is simply a convenience method. The two following calls are equivalent:
    * {{{
    *   val str: CsvSource[String] = CsvSource[String]
    *   val str2: CsvSource[String] = implicitly[CsvSource[String]]
    * }}}
    */
  def apply[A](implicit ev: CsvSource[A]): CsvSource[A] = macro imp.summon[CsvSource[A]]

  /** Turns the specified function into a [[CsvSource]].
    *
    * Note that it's usually better to compose an existing instance through [[CsvSource.contramap]] or
    * [[CsvSource.contramapResult]] rather than create one from scratch. For example:
    * {{{
    *   val urlInput: CsvSource[URL] = CsvSource[InputStream].contramap((url: URL) ⇒ url.openStream())
    * }}}
    *
    * @see [[CsvSource.contramap]]
    * @see [[CsvSource.contramapResult]]
    */
  def from[A](f: A ⇒ ParseResult[Reader]): CsvSource[A] = new CsvSource[A] {
    override def open(a: A) = f(a)
  }

  implicit def fromResource[A: ReaderResource]: CsvSource[A] =
    CsvSource.from(a ⇒ ReaderResource[A].open(a).leftMap(e ⇒ ParseError.IOError(e.getMessage, e.getCause)))
}
