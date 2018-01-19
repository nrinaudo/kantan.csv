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

/** Provides syntax for decoding CSV cells as values.
  *
  * Importing `kantan.csv.ops._` will add the following methods to `String`:
  *  - [[decodeCsv]]
  *  - [[unsafeDecodeCsv]]
  */
final class CsvCellDecoderOps(val s: String) {

  /** Decodes a CSV cell as a value of type `A`.
    *
    * @example
    * {{{
    * scala> "1".decodeCsv[Option[Int]]
    * res0: kantan.csv.DecodeResult[Option[Int]] = Right(Some(1))
    * }}}
    */
  def decodeCsv[A: CellDecoder]: DecodeResult[A] =
    CellDecoder[A].decode(s)

  /** Decodes a CSV cell as a value of type `A`.
    *
    * @example
    * {{{
    * scala> "1".unsafeDecodeCsv[Option[Int]]
    * res0: Option[Int] = Some(1)
    * }}}
    *
    * Note that this method is unsafe and will throw an exception if the cell's value is not a valid `A`. Prefer
    * [[decodeCsv]] whenever possible.
    */
  def unsafeDecodeCsv[A: CellDecoder]: A = CellDecoder[A].unsafeDecode(s)
}

trait ToCsvCellDecoderOps {
  implicit def toCsvCellDecoderOps(s: String): CsvCellDecoderOps =
    new CsvCellDecoderOps(s)
}

object cellDecoder extends ToCsvCellDecoderOps
