/*
 * Copyright 2017 Nicolas Rinaudo
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

package kantan.csv.ops

import kantan.csv._
import kantan.csv.engine.ReaderEngine
import scala.collection.generic.CanBuildFrom

/** Provides useful syntax for types that have implicit instances of [[CsvSource]] in scope.
  *
  * The most common use case is to turn a value into a [[CsvReader]] through [[asCsvReader]]:
  * {{{
  * scala> import kantan.csv._
  *
  * scala> "1,2,3\n4,5,6".asCsvReader[List[Int]](',', false).toList
  * res0: List[ReadResult[List[Int]]] = List(Success(List(1, 2, 3)), Success(List(4, 5, 6)))
  * }}}
  *
  * A slightly less common use case is to load an entire CSV file in memory through [[readCsv]]:
  * {{{
  * scala> "1,2,3\n4,5,6".readCsv[List, List[Int]](',', false)
  * res1: List[ReadResult[List[Int]]] = List(Success(List(1, 2, 3)), Success(List(4, 5, 6)))
  * }}}
  *
  * Unsafe versions of these methods are also available, even if usually advised against.
  */
final class CsvSourceOps[A](val a: A) extends AnyVal {
  /** Opens a [[CsvReader]] on the underlying resource.
    *
    * For example:
    * {{{
    * scala> import kantan.csv._
    *
    * scala> "1,2,3\n4,5,6".asCsvReader[List[Int]](',', false).toList
    * res0: List[ReadResult[List[Int]]] = List(Success(List(1, 2, 3)), Success(List(4, 5, 6)))
    * }}}
    *
    * This is a convenience method only, and strictly equivalent to:
    * {{{
    * scala> CsvSource[String].reader[List[Int]]("1,2,3\n4,5,6", ',', false).toList
    * res1: List[ReadResult[List[Int]]] = List(Success(List(1, 2, 3)), Success(List(4, 5, 6)))
    * }}}
    *
    * @param  sep    column separator.
    * @param  header whether or not to skip the first row.
    * @tparam B      type each row will be decoded as.
    */
  def asCsvReader[B: RowDecoder](sep: Char, header: Boolean)
                                (implicit ia: CsvSource[A], e: ReaderEngine): CsvReader[ReadResult[B]] =
    ia.reader[B](a, sep, header)

  /** Opens an unsafe [[CsvReader]] on the underlying resource.
    *
    * For example:
    * {{{
    * scala> import kantan.csv._
    *
    * scala> "1,2,3\n4,5,6".asUnsafeCsvReader[List[Int]](',', false).toList
    * res0: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * This is a convenience method only, and strictly equivalent to:
    * {{{
    * scala> CsvSource[String].unsafeReader[List[Int]]("1,2,3\n4,5,6", ',', false).toList
    * res1: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * @param  sep    column separator.
    * @param  header whether or not to skip the first row.
    * @tparam B      type each row will be decoded as.
    */
  def asUnsafeCsvReader[B: RowDecoder](sep: Char, header: Boolean)
                                      (implicit ia: CsvSource[A], e: ReaderEngine): CsvReader[B] =
    ia.unsafeReader[B](a, sep, header)

  /** Reads the underlying resource as a CSV stream.
    *
    * For example:
    * {{{
    * scala> import kantan.csv._
    *
    * scala> "1,2,3\n4,5,6".readCsv[List, List[Int]](',', false)
    * res0: List[ReadResult[List[Int]]] = List(Success(List(1, 2, 3)), Success(List(4, 5, 6)))
    * }}}
    *
    * This is a convenience method only, and strictly equivalent to:
    * {{{
    * scala> CsvSource[String].read[List, List[Int]]("1,2,3\n4,5,6", ',', false)
    * res1: List[ReadResult[List[Int]]] = List(Success(List(1, 2, 3)), Success(List(4, 5, 6)))
    * }}}
    *
    * @param  sep    column separator.
    * @param  header whether or not to skip the first row.
    * @tparam B      type each row will be decoded as.
    * @tparam C      type of the collection in which the decoded CSV data will be stored.
    */
  def readCsv[C[_], B: RowDecoder](sep: Char, header: Boolean)
                                  (implicit ia: CsvSource[A], e: ReaderEngine,
                                   cbf: CanBuildFrom[Nothing, ReadResult[B], C[ReadResult[B]]]): C[ReadResult[B]] =
    ia.read[C, B](a, sep, header)

  /** Reads the underlying resource as a CSV stream (unsafely).
    *
    * For example:
    * {{{
    * scala> import kantan.csv._
    *
    * scala> "1,2,3\n4,5,6".unsafeReadCsv[List, List[Int]](',', false)
    * res0: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * This is a convenience method only, and strictly equivalent to:
    * {{{
    * scala> CsvSource[String].unsafeRead[List, List[Int]]("1,2,3\n4,5,6", ',', false)
    * res1: List[List[Int]] = List(List(1, 2, 3), List(4, 5, 6))
    * }}}
    *
    * @param  sep    column separator.
    * @param  header whether or not to skip the first row.
    * @tparam B      type each row will be decoded as.
    * @tparam C      type of the collection in which the decoded CSV data will be stored.
    */
  def unsafeReadCsv[C[_], B: RowDecoder](sep: Char, header: Boolean)
                                        (implicit ia: CsvSource[A], e: ReaderEngine,
                                         cbf: CanBuildFrom[Nothing, B, C[B]]): C[B] =
    ia.unsafeRead[C, B](a, sep, header)
}

trait ToCsvSourceOps {
  implicit def toCsvInputOps[A](a: A): CsvSourceOps[A] = new CsvSourceOps(a)
}

object source extends ToCsvSourceOps
