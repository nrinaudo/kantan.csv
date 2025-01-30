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

import kantan.csv.CellEncoder

/** Provides syntax for encoding values as CSV cells.
  *
  * Importing `kantan.csv.ops._` will add [[asCsvCell]] to any type `A` such that there exists an implicit
  * `CellDecoder[A]` in scope.
  */
final class CsvCellEncoderOps[A: CellEncoder](val a: A) {

  /** Encodes a value as a CSV cell.
    *
    * @example
    *   {{{
    * scala> 1.asCsvCell
    * res0: String = 1
    *   }}}
    */
  def asCsvCell: String =
    CellEncoder[A].encode(a)
}

trait ToCsvCellEncoderOps {
  implicit def toCsvCellEncoderOps[A: CellEncoder](a: A): CsvCellEncoderOps[A] =
    new CsvCellEncoderOps(a)
}

object cellEncoder extends ToCsvCellEncoderOps
