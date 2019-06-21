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
import kantan.codecs.collection._
import kantan.codecs.resource.{ReaderResource, ResourceIterator}
import kantan.csv.DecodeError.{OutOfBounds, TypeError}
import kantan.csv.ParseError.{IOError, NoSuchElement}
import kantan.csv.engine.ReaderEngine

/** Turns instances of `S` into valid sources of CSV data.
  *
  * Instances of [[CsvSource]] are rarely used directly. The preferred, idiomatic way is to use the implicit syntax
  * provided by [[ops.CsvSourceOps CsvSourceOps]], brought in scope by importing `kantan.csv.ops._`.
  *
  * See the [[CsvSource$ companion object]] for default implementations and construction methods.
  */
trait CsvSource[-S] extends Serializable { self =>

  /** Turns the specified `S` into a `Reader`.
    *
    * Implementations of this method *must* be safe: all non-fatal exceptions should be caught and wrapped in an
    * [[ParseError.IOError]]. This is easily achieved by wrapping unsafe code in a call to [[ParseResult.apply]].
    *
    * @param s instance of `S` to turn into a [[CsvSource]].
    */
  def open(s: S): ParseResult[Reader]

  @deprecated("use reader(S, CsvConfiguration) instead", "0.1.18")
  def reader[A: HeaderDecoder](s: S, sep: Char, header: Boolean)(implicit e: ReaderEngine): CsvReader[ReadResult[A]] =
    reader(s, rfc.withCellSeparator(sep).withHeader(header))

  /** Turns the specified `S` into an iterator on `ReadResult[A]`.
    *
    * This method is "safe", in that it does not throw exceptions when errors are encountered. This comes with the small
    * cost of having each row wrapped in a [[ReadResult]] that then need to be unpacked. See
    * [[unsafeReader[A](s:S,conf:kantan\.csv\.CsvConfiguration* unsafeReader]] for an alternative.
    *
    * @example
    * {{{
    * scala> CsvSource[String].reader[List[Int]]("1,2,3\n4,5,6", rfc).toList
    * res0: List[ReadResult[List[Int]]] = List(Right(List(1, 2, 3)), Right(List(4, 5, 6)))
    * }}}
    *
    * @param s instance of `S` that will be opened an parsed.
    * @param conf CSV parsing behaviour.
    * @tparam A type to parse each row as. This must have a corresponding implicit [[HeaderDecoder]] instance in scope.
    */
  def reader[A: HeaderDecoder](s: S, conf: CsvConfiguration)(implicit e: ReaderEngine): CsvReader[ReadResult[A]] =
    open(s)
      .map(reader => CsvReader(reader, conf))
      .left
      .map(error => ResourceIterator(ReadResult.failure(error)))
      .merge

  @deprecated("use unsafeReader(S, CsvConfiguration) instead", "0.1.18")
  def unsafeReader[A: HeaderDecoder](s: S, sep: Char, header: Boolean)(implicit engine: ReaderEngine): CsvReader[A] =
    unsafeReader(s, rfc.withCellSeparator(sep).withHeader(header))

  /** Turns the specified `S` into an iterator on `A`.
    *
    * This is the "unsafe" version of [[reader[A](s:S,conf:kantan\.csv\.CsvConfiguration* reader]]: it will throw as
    * soon as an error is encountered.
    *
    * @example
    * {{{
    * scala> CsvSource[String].unsafeReader[List[Int]]("1,2,3\n4,5,6", rfc).toList
    * res0: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * @param s instance of `S` that will be opened an parsed.
    * @param conf CSV parsing behaviour.
    * @tparam A type to parse each row as. This must have a corresponding implicit [[HeaderDecoder]] instance in scope.
    */
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def unsafeReader[A: HeaderDecoder](s: S, conf: CsvConfiguration)(implicit engine: ReaderEngine): CsvReader[A] =
    reader[A](s, conf).map(_.left.map {
      case e @ TypeError(msg) => throw Option(e.getCause).getOrElse(new IllegalArgumentException(msg))
      case NoSuchElement      => throw new NoSuchElementException
      case e @ IOError(msg)   => throw Option(e.getCause).getOrElse(new IOException(msg))
      case OutOfBounds(index) => throw new ArrayIndexOutOfBoundsException(index)
    }.merge)

  @deprecated("use read(S, CsvConfiguration) instead", "0.1.18")
  def read[C[_], A: HeaderDecoder](s: S, sep: Char, header: Boolean)(
    implicit e: ReaderEngine,
    factory: Factory[ReadResult[A], C[ReadResult[A]]]
  ): C[ReadResult[A]] =
    read(s, rfc.withCellSeparator(sep).withHeader(header))

  /** Reads the entire CSV data into a collection.
    *
    * This method is "safe", in that it does not throw exceptions when errors are encountered. This comes with the small
    * cost of having each row wrapped in a [[ReadResult]] that then need to be unpacked. See
    * [[unsafeRead[C[_],A](s:S,conf:kantan\.csv\.CsvConfiguration*  unsafeRead]] for an
    * alternative.
    *
    * @example
    * {{{
    * scala> CsvSource[String].read[List, List[Int]]("1,2,3\n4,5,6", rfc)
    * res0: List[ReadResult[List[Int]]] = List(Right(List(1, 2, 3)), Right(List(4, 5, 6)))
    * }}}
    *
    * @param s instance of `S` that will be opened an parsed.
    * @param conf CSV parsing behaviour.
    * @tparam C collection type in which to parse the specified `S`.
    * @tparam A type in which to parse each row.
    */
  def read[C[_], A: HeaderDecoder](
    s: S,
    conf: CsvConfiguration
  )(implicit e: ReaderEngine, factory: Factory[ReadResult[A], C[ReadResult[A]]]): C[ReadResult[A]] =
    reader(s, conf).to(factory)

  @deprecated("use unsafeRead(S, CsvConfiguration) instead", "0.1.18")
  def unsafeRead[C[_], A: HeaderDecoder](s: S, sep: Char, header: Boolean)(
    implicit e: ReaderEngine,
    factory: Factory[A, C[A]]
  ): C[A] =
    unsafeRead(s, rfc.withCellSeparator(sep).withHeader(header))

  /** Reads the entire CSV data into a collection.
    *
    * This is the "unsafe" version of [[read[C[_],A](s:S,conf:kantan\.csv\.CsvConfiguration* read]]: it will throw as
    * soon as an error is encountered.
    *
    * @example
    * {{{
    * scala> CsvSource[String].unsafeRead[List, List[Int]]("1,2,3\n4,5,6", rfc)
    * res0: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * @param s instance of `S` that will be opened an parsed.
    * @param conf CSV parsing behaviour.
    * @tparam C collection type in which to parse the specified `S`.
    * @tparam A type in which to parse each row.
    */
  def unsafeRead[C[_], A: HeaderDecoder](
    s: S,
    conf: CsvConfiguration
  )(implicit e: ReaderEngine, factory: Factory[A, C[A]]): C[A] =
    unsafeReader(s, conf).to(factory)

  /** Turns an instance of `CsvSource[S]` into one of `CsvSource[T]`.
    *
    * This allows developers to adapt existing instances of [[CsvSource]] rather than write new ones from scratch.
    *
    * Note that this method assumes that the transformation from `T` to `S` is safe. If it fail, one should use
    * [[econtramap]] instead.
    *
    * @example
    * {{{
    * scala> case class StringWrapper(value: String)
    *
    * scala> implicit val wrapperSource: CsvSource[StringWrapper] = CsvSource[String].contramap(_.value)
    *
    * scala> CsvSource[StringWrapper].unsafeRead[List, List[Int]](StringWrapper("1,2,3\n4,5,6"), rfc)
    * res0: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * @see [[econtramap]]
    */
  def contramap[T](f: T => S): CsvSource[T] = CsvSource.from(f andThen self.open)

  /** Turns an instance of `CsvSource[S]` into one of `CsvSource[T]`.
    *
    * This allows developers to adapt existing instances of [[CsvSource]] rather than write new ones from scratch.
    *
    * @example
    * {{{
    * scala> case class StringWrapper(value: String)
    *
    * scala> implicit val source: CsvSource[StringWrapper] = CsvSource[String].econtramap(s => ParseResult(s.value))
    *
    * scala> CsvSource[StringWrapper].unsafeRead[List, List[Int]](StringWrapper("1,2,3\n4,5,6"), rfc)
    * res0: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * Note that if the transformation from `T` to `S` is safe, it's better to use [[contramap]] and bypass the error
    * handling mechanism altogether.
    *
    * @see [[contramap]]
    */
  def econtramap[SS <: S, T](f: T => ParseResult[SS]): CsvSource[T] =
    CsvSource.from(t => f(t).flatMap(self.open))

  @deprecated("Use econtramap instead", "0.3.2")
  def contramapResult[SS <: S, T](f: T => ParseResult[SS]): CsvSource[T] = econtramap(f)
}

/** Defines convenience methods for creating and retrieving instances of [[CsvSource]].
  *
  * Implicit default implementations of standard types are also declared here, always bringing them in scope with a low
  * priority.
  *
  * These default implementations can also be useful when writing more complex instances: if you need to write a
  * `CsvSource[T]` and have both a `CsvSource[S]` and a `T ⇒ S`, you need just use [[CsvSource.contramap]] to create
  * your implementation.
  */
object CsvSource {

  /** Summons an implicit instance of `CsvSource[A]` if one can be found.
    *
    * This is basically a less verbose, slightly faster version of `implicitly`.
    */
  def apply[A](implicit ev: CsvSource[A]): CsvSource[A] = macro imp.summon[CsvSource[A]]

  /** Turns the specified function into a [[CsvSource]].
    *
    * Note that it's usually better to compose an existing instance through [[CsvSource.contramap]] or
    * [[CsvSource.econtramap]] rather than create one from scratch.
    *
    * @example
    * {{{
    *   val urlInput: CsvSource[URL] = CsvSource[InputStream].contramap((url: URL) ⇒ url.openStream())
    * }}}
    *
    * @see [[CsvSource.contramap]]
    * @see [[CsvSource.econtramap]]
    */
  def from[A](f: A => ParseResult[Reader]): CsvSource[A] = new CsvSource[A] {
    override def open(a: A): ParseResult[Reader] = f(a)
  }

  implicit def fromResource[A: ReaderResource]: CsvSource[A] =
    CsvSource.from(a => ReaderResource[A].open(a).left.map(e => ParseError.IOError(e.getMessage, e.getCause)))
}
