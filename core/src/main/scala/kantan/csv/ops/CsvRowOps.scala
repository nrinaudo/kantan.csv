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
import kantan.csv.engine.WriterEngine

/** Provides useful syntax for working with CSV rows.
  *
  * Writing a single row as a `String` is a surprisingly recurrent feature request. This is how to do it:
  * {{{
  * scala> import kantan.csv.rfc
  *
  * scala> (1, 2, 3).asCsvRow(rfc)
  * res0: String = 1,2,3
  * }}}
  */
final class CsvRowOps[A: RowEncoder](val a: A) {
  @deprecated("use asCsvRow(CsvConfiguration) instead", "0.1.18")
  def asCsvRow(sep: Char)(implicit e: WriterEngine): String =
    asCsvRow(rfc.withCellSeparator(sep))

  def asCsvRow(conf: CsvConfiguration)(implicit e: WriterEngine): String =
    Seq(a).asCsv(conf).trim
}

trait ToCsvRowOps {
  implicit def toCsvRowOps[A: HeaderEncoder](a: A): CsvRowOps[A] = new CsvRowOps(a)(HeaderEncoder[A].rowEncoder)
}

object csvRow extends ToCsvRowOps
