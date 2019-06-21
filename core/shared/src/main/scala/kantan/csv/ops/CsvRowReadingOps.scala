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

/** Provides syntax for decoding a string as a CSV row. */
final class CsvRowReadingOps[A: CsvSource](a: A) {

  /** Parses a string as a single CSV row.
    *
    * @example
    * {{{
    * scala> import kantan.csv._
    *
    * scala> "1,2,3".readCsvRow[(Int, Int, Int)](rfc)
    * res0: ReadResult[(Int, Int, Int)] = Right((1,2,3))
    * }}}
    */
  def readCsvRow[B: RowDecoder](conf: CsvConfiguration)(implicit e: ReaderEngine): ReadResult[B] = {
    val reader = a.asCsvReader[B](conf)

    reader.next.flatMap { res =>
      // Slight abuse of `no such element` to mean that we're not working with a single row.
      if(reader.hasNext) ParseResult.noSuchElement
      else ReadResult.success(res)
    }
  }

  /** Parses a string as a single CSV row.
    *
    * @example
    * {{{
    * scala> import kantan.csv._
    *
    * scala> "1,2,3".unsafeReadCsvRow[(Int, Int, Int)](rfc)
    * res0: (Int, Int, Int) = (1,2,3)
    * }}}
    *
    * Note that this method is unsafe and will throw an exception if the string value is not a valid `A`. Prefer
    * [[readCsvRow]] whenever possible.
    */
  @SuppressWarnings(Array("org.wartremover.warts.StringPlusAny"))
  def unsafeReadCsvRow[B: RowDecoder](conf: CsvConfiguration)(implicit e: ReaderEngine): B =
    readCsvRow[B](conf).fold(
      error => sys.error(s"Failed to decode value $a: $error"),
      w => w
    )
}

trait ToCsvRowReadingOps {
  implicit def toCsvRowReadingOps[A: CsvSource](a: A): CsvRowReadingOps[A] = new CsvRowReadingOps(a)
}
