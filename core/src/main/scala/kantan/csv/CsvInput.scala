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
import java.net.{URI, URL}
import java.nio.file.{Files, Path}
import kantan.codecs.{ResourceIterator, Result}
import kantan.csv.DecodeError.{OutOfBounds, TypeError}
import kantan.csv.ParseError.{IOError, NoSuchElement}
import kantan.csv.engine.ReaderEngine
import scala.collection.generic.CanBuildFrom
import scala.io.Codec

/** Turns instances of `S` into valid sources of CSV data.
  *
  * Instances of [[CsvInput]] are rarely used directly. The preferred, idiomatic way is to use the implicit syntax
  * provided by [[ops.CsvInputOps CsvInputOps]], brought in scope by importing `kantan.csv.ops._`.
  *
  * See the [[CsvInput$ companion object]] for default implementations and construction methods.
  */
trait CsvInput[-S] extends Serializable { self ⇒
  /** Turns the specified `S` into a `Reader`.
    *
    * Implementations of this method *must* be safe: all non-fatal exceptions should be caught and wrapped in an
    * [[ParseError.IOError]]. This is easily achieved by wrapping unsafe code in a call to [[ParseResult.apply]].
    *
    * @param s instance of `S` to turn into a [[CsvInput]].
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


  /** Turns an instance of `CsvInput[S]` into one of `CsvInput[T]`.
    *
    * This allows developers to adapt existing instances of [[CsvInput]] rather than write one from scratch.
    * One could, for example, write `CsvInput[String]` by basing it on `CsvInput[Reader]`:
    * {{{
    *   val urlInput: CsvInput[String] = CsvInput[Reader].contramap((s: String) ⇒ new java.io.StringReader(s))
    * }}}
    *
    * Note that this method assumes that the transformation from `T` to `S` is safe. If it fail, one should use
    * [[contramapResult]] instead.
    *
    * @see [[contramapResult]]
    */
  def contramap[T](f: T ⇒ S): CsvInput[T] = CsvInput((t: T) ⇒ self.open(f(t)))

  /** Turns an instance of `CsvInput[S]` into one of `CsvInput[T]`.
    *
    * This allows developers to adapt existing instances of [[CsvInput]] rather than write one from scratch.
    * One could, for example, write `CsvInput[URL]` by basing it on `CsvInput[InputStream]`:
    * {{{
    *   val urlInput: CsvInput[URL] = CsvInput[InputStream].contramap((url: URL) ⇒ url.openStream())
    * }}}
    *
    * Note that if the transformation from `T` to `S` is safe, it's better to use [[contramap]] and bypass the error
    * handling mechanism altogether.
    *
    * @see [[contramap]]
    */
  def contramapResult[T](f: T ⇒ ParseResult[S]): CsvInput[T] = CsvInput((t: T) ⇒ f(t).flatMap(self.open))
}

trait LowPriorityCsvInputs

/** Defines convenience methods for creating and retrieving instances of [[CsvInput]].
  *
  * Implicit default implementations of standard types are also declared here, always bringing them in scope with a low
  * priority.
  *
  * These default implementations can also be useful when writing more complex instances: if you need to write a
  * `CsvInput[T]` and have both a `CsvInput[S]` and a `T ⇒ S`, you need just use [[CsvInput.contramap]] to create
  * your implementation.
  */
object CsvInput extends LowPriorityCsvInputs {
  /** Summons an implicit instance of `CsvInput[A]` if one can be found.
    *
    * This is simply a convenience method. The two following calls are equivalent:
    * {{{
    *   val str: CsvInput[String] = CsvInput[String]
    *   val str2: CsvInput[String] = implicitly[CsvInput[String]]
    * }}}
    */
  def apply[A](implicit ia: CsvInput[A]): CsvInput[A] = ia

  /** Turns the specified function into a [[CsvInput]].
    *
    * Note that it's usually better to compose an existing instance through [[CsvInput.contramap]] or
    * [[CsvInput.contramapResult]] rather than create one from scratch. For example:
    * {{{
    *   val urlInput: CsvInput[URL] = CsvInput[InputStream].contramap((url: URL) ⇒ url.openStream())
    * }}}
    *
    * @see [[CsvInput.contramap]]
    * @see [[CsvInput.contramapResult]]
    */
  def apply[A](f: A ⇒ ParseResult[Reader]): CsvInput[A] = new CsvInput[A] {
    override def open(a: A) = f(a)
  }

  /** Turns any `java.io.Reader` into a source of CSV data. */
  implicit def reader: CsvInput[Reader] = CsvInput(r ⇒ ParseResult.success(r))

  /** Turns any `java.io.InputStream` into a source of CSV data. */
  implicit def inputStream(implicit codec: Codec): CsvInput[InputStream] =
    reader.contramap(i ⇒ new InputStreamReader(i, codec.charSet))
  /** Turns any `java.io.File` into a source of CSV data. */
  implicit def file(implicit codec: Codec): CsvInput[File] =
    inputStream.contramapResult(f ⇒ ParseResult(new FileInputStream(f)))
  /** Turns any array of bytes into a source of CSV data. */
  implicit def bytes(implicit codec: Codec): CsvInput[Array[Byte]] =
    inputStream.contramap(bs ⇒ new ByteArrayInputStream(bs))
  /** Turns any `java.net.URL` into a source of CSV data. */
  implicit def url(implicit codec: Codec): CsvInput[URL] = inputStream.contramapResult(u ⇒ ParseResult(u.openStream()))
  /** Turns any `java.net.URI` into a source of CSV data. */
  implicit def uri(implicit codec: Codec): CsvInput[URI] = url.contramap(_.toURL)
  /** Turns any array of chars into a source of CSV data. */
  implicit val chars: CsvInput[Array[Char]] = reader.contramap(cs ⇒ new CharArrayReader(cs))
  /** Turns any string into a source of CSV data. */
  implicit val string: CsvInput[String] = reader.contramap(s ⇒ new StringReader(s))
  /** Turns any path into a source of CSV data. */
  implicit def path(implicit codec: Codec): CsvInput[Path] =
    reader.contramapResult(p ⇒ ParseResult(Files.newBufferedReader(p, codec.charSet)))
}
