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
package ops

import engine.ReaderEngine
import kantan.codecs.collection._

/** Provides useful syntax for types that have implicit instances of [[CsvSource]] in scope.
  *
  * The most common use case is to turn a value into a [[CsvReader]]:
  * {{{
  * scala> import kantan.csv._
  *
  * scala> "1,2,3\n4,5,6".asCsvReader[List[Int]](rfc).toList
  * res0: List[ReadResult[List[Int]]] = List(Right(List(1, 2, 3)), Right(List(4, 5, 6)))
  * }}}
  *
  * A slightly less common use case is to load an entire CSV file in memory:
  * {{{
  * scala> "1,2,3\n4,5,6".readCsv[List, List[Int]](rfc)
  * res1: List[ReadResult[List[Int]]] = List(Right(List(1, 2, 3)), Right(List(4, 5, 6)))
  * }}}
  *
  * Unsafe versions of these methods are also available, even if usually advised against.
  */
final class CsvSourceOps[A: CsvSource](val a: A) {
  @deprecated("use asCsvReader(CsvConfiguration) instead", "0.1.18")
  def asCsvReader[B: HeaderDecoder](sep: Char, header: Boolean)(implicit e: ReaderEngine): CsvReader[ReadResult[B]] =
    asCsvReader(rfc.withCellSeparator(sep).withHeader(header))

  /** Opens a [[CsvReader]] on the underlying resource.
    *
    * This is a convenience method only, and strictly equivalent to:
    * {{{
    * scala> import kantan.csv._
    *
    * scala> CsvSource[String].reader[List[Int]]("1,2,3\n4,5,6", rfc).toList
    * res1: List[ReadResult[List[Int]]] = List(Right(List(1, 2, 3)), Right(List(4, 5, 6)))
    * }}}
    *
    * @example
    * {{{
    * scala> import kantan.csv._
    *
    * scala> "1,2,3\n4,5,6".asCsvReader[List[Int]](rfc).toList
    * res0: List[ReadResult[List[Int]]] = List(Right(List(1, 2, 3)), Right(List(4, 5, 6)))
    * }}}
    *
    * @param  conf   CSV parsing behaviour.
    * @tparam B      type each row will be decoded as.
    */
  def asCsvReader[B: HeaderDecoder](conf: CsvConfiguration)(implicit e: ReaderEngine): CsvReader[ReadResult[B]] =
    CsvSource[A].reader[B](a, conf)

  @deprecated("use asUnsafeCsvReader(CsvConfiguration) instead", "0.1.18")
  def asUnsafeCsvReader[B: HeaderDecoder](sep: Char, header: Boolean)(implicit e: ReaderEngine): CsvReader[B] =
    asUnsafeCsvReader(rfc.withCellSeparator(sep).withHeader(header))

  /** Opens an unsafe [[CsvReader]] on the underlying resource.
    *
    * This is a convenience method only, and strictly equivalent to:
    * {{{
    * scala> import kantan.csv._
    *
    * scala> CsvSource[String].unsafeReader[List[Int]]("1,2,3\n4,5,6", rfc).toList
    * res1: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * @example
    * {{{
    * scala> import kantan.csv._
    *
    * scala> "1,2,3\n4,5,6".asUnsafeCsvReader[List[Int]](rfc).toList
    * res0: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * @param  conf   CSV parsing behaviour.
    * @tparam B      type each row will be decoded as.
    */
  def asUnsafeCsvReader[B: HeaderDecoder](conf: CsvConfiguration)(implicit e: ReaderEngine): CsvReader[B] =
    CsvSource[A].unsafeReader[B](a, conf)

  @deprecated("use readCsv(CsvConfiguration) instead", "0.1.18")
  def readCsv[C[_], B: HeaderDecoder](
    sep: Char,
    header: Boolean
  )(implicit e: ReaderEngine, factory: Factory[ReadResult[B], C[ReadResult[B]]]): C[ReadResult[B]] =
    readCsv(rfc.withCellSeparator(sep).withHeader(header))

  /** Reads the underlying resource as a CSV stream.
    *
    * This is a convenience method only, and strictly equivalent to:
    * {{{
    * scala> import kantan.csv._
    *
    * scala> CsvSource[String].read[List, List[Int]]("1,2,3\n4,5,6", rfc)
    * res1: List[ReadResult[List[Int]]] = List(Right(List(1, 2, 3)), Right(List(4, 5, 6)))
    * }}}
    *
    * @example
    * {{{
    * scala> import kantan.csv._
    *
    * scala> "1,2,3\n4,5,6".readCsv[List, List[Int]](rfc)
    * res0: List[ReadResult[List[Int]]] = List(Right(List(1, 2, 3)), Right(List(4, 5, 6)))
    * }}}
    *
    * @param  conf   CSV parsing behaviour.
    * @tparam B      type each row will be decoded as.
    * @tparam C      type of the collection in which the decoded CSV data will be stored.
    */
  def readCsv[C[_], B: HeaderDecoder](
    conf: CsvConfiguration
  )(implicit e: ReaderEngine, factory: Factory[ReadResult[B], C[ReadResult[B]]]): C[ReadResult[B]] =
    CsvSource[A].read[C, B](a, conf)

  @deprecated("use unsafeReadCsv(CsvConfiguration) instead", "0.1.18")
  def unsafeReadCsv[C[_], B: HeaderDecoder](
    sep: Char,
    header: Boolean
  )(e: ReaderEngine, factory: Factory[B, C[B]]): C[B] =
    unsafeReadCsv(rfc.withCellSeparator(sep).withHeader(header))(HeaderDecoder[B], e, factory)

  /** Reads the underlying resource as a CSV stream (unsafely).
    *
    * This is a convenience method only, and strictly equivalent to:
    * {{{
    * scala> import kantan.csv._
    *
    * scala> CsvSource[String].unsafeRead[List, List[Int]]("1,2,3\n4,5,6", rfc)
    * res1: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * @example
    * {{{
    * scala> import kantan.csv._
    *
    * scala> "1,2,3\n4,5,6".unsafeReadCsv[List, List[Int]](rfc)
    * res0: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * @param  conf   CSV parsing behaviour.
    * @tparam B      type each row will be decoded as.
    * @tparam C      type of the collection in which the decoded CSV data will be stored.
    */
  def unsafeReadCsv[C[_], B: HeaderDecoder](
    conf: CsvConfiguration
  )(implicit e: ReaderEngine, factory: Factory[B, C[B]]): C[B] =
    CsvSource[A].unsafeRead[C, B](a, conf)
}

trait ToCsvSourceOps {
  implicit def toCsvInputOps[A: CsvSource](a: A): CsvSourceOps[A] = new CsvSourceOps(a)
}

object source extends ToCsvSourceOps
