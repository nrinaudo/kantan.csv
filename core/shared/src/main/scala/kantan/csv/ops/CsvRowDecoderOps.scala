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

package kantan.csv.ops

import kantan.csv.DecodeResult
import kantan.csv.RowDecoder

/** Provides syntax for decoding CSV rows as values.
  *
  * Importing `kantan.csv.ops._` will add the following methods to `Seq[String]`:
  *   - [[decodeCsv]]
  *   - [[unsafeDecodeCsv]]
  */
final class CsvRowDecoderOps(val ss: Seq[String]) {

  /** Decodes a CSV row as a value of type `A`.
    *
    * @example
    *   {{{
    * scala> Seq("1", "2", "3").decodeCsv[(Int, Int, Int)]
    * res0: kantan.csv.DecodeResult[(Int, Int, Int)] = Right((1,2,3))
    *   }}}
    */
  def decodeCsv[A: RowDecoder]: DecodeResult[A] =
    RowDecoder[A].decode(ss)

  /** Decodes a CSV row as a value of type `A`.
    *
    * @example
    *   {{{
    * scala> Seq("1", "2", "3").unsafeDecodeCsv[(Int, Int, Int)]
    * res0: (Int, Int, Int) = (1,2,3)
    *   }}}
    *
    * Note that this method is unsafe and will throw an exception if the row's value is not a valid `A`. Prefer
    * [[decodeCsv]] whenever possible.
    */
  def unsafeDecodeCsv[A: RowDecoder]: A =
    RowDecoder[A].unsafeDecode(ss)
}

trait ToCsvRowDecoderOps {
  implicit def toCsvRowDecoderOps(ss: Seq[String]): CsvRowDecoderOps =
    new CsvRowDecoderOps(ss)
}

object rowDecoder extends ToCsvRowDecoderOps
