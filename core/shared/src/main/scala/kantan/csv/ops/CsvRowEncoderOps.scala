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

import kantan.csv.HeaderEncoder
import kantan.csv.RowEncoder

/** Provides syntax for encoding values as CSV rows.
  *
  * Importing `kantan.csv.ops._` will add [[asCsvRow]] to any type `A` such that there exists an implicit
  * `RowEncoder[A]` in scope.
  */
final class CsvRowEncoderOps[A: RowEncoder](val a: A) {

  /** Encodes a value as a CSV row.
    *
    * @example
    *   {{{
    * scala> List(1, 2, 3).asCsvRow
    * res0: Seq[String] = List(1, 2, 3)
    *   }}}
    */
  def asCsvRow: Seq[String] =
    RowEncoder[A].encode(a)
}

trait ToCsvRowEncoderOps {
  implicit def toCsvRowEncoderOps[A: HeaderEncoder](a: A): CsvRowEncoderOps[A] =
    new CsvRowEncoderOps(a)(HeaderEncoder[A].rowEncoder)
}

object rowEncoder extends ToCsvRowEncoderOps
