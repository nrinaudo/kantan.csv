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

import kantan.csv.CsvConfiguration
import kantan.csv.CsvWriter
import kantan.csv.HeaderEncoder
import kantan.csv.engine.WriterEngine
import kantan.csv.rfc

import java.io.StringWriter

/** Provides syntax for turning collections into CSV strings. */
final class CsvRowsOps[A: HeaderEncoder](val as: IterableOnce[A]) {
  @deprecated("use asCsv(CsvConfiguration) instead", "0.1.18")
  def asCsv(sep: Char, header: String*)(implicit e: WriterEngine): String =
    asCsv(rfc.withCellSeparator(sep).withHeader(header: _*))

  /** Writes collections of `A` as a CSV string.
    *
    * @example
    *   {{{
    * scala> List(List(1, 2, 3), List(4, 5, 6)).asCsv(rfc)
    *   }}}
    */
  def asCsv(conf: CsvConfiguration)(implicit e: WriterEngine): String = {
    val out = new StringWriter()
    CsvWriter(out, conf).write(as).close()
    out.toString
  }
}

trait ToCsvRowsOps {
  implicit def toCsvRowsOps[A: HeaderEncoder](as: IterableOnce[A]): CsvRowsOps[A] =
    new CsvRowsOps(as)
}

object csvRows extends ToCsvRowsOps
