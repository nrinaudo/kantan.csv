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

import engine.WriterEngine

/** Provides syntax for encoding single CSV rows as a string.
  *
  * Writing a single row as a `String` is a surprisingly recurrent feature request. This is how to do it:
  *
  * {{{
  * scala> import kantan.csv.rfc
  *
  * scala> (1, 2, 3).writeCsvRow(rfc)
  * res0: String = 1,2,3
  * }}}
  */
final class CsvRowWritingOps[A: RowEncoder](a: A) {
  @deprecated("use writeCsvRow(CsvConfiguration) instead", "0.1.18")
  def writeCsvRow(sep: Char)(implicit e: WriterEngine): String =
    writeCsvRow(rfc.withCellSeparator(sep))

  def writeCsvRow(conf: CsvConfiguration)(implicit e: WriterEngine): String =
    Seq(a).asCsv(conf).trim
}

trait ToCsvRowWritingOps {
  implicit def toCsvRowWritingOps[A: RowEncoder](a: A): CsvRowWritingOps[A] = new CsvRowWritingOps(a)
}
