/*
 * Copyright 2016 Nicolas Rinaudo
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

import java.io.StringWriter
import kantan.csv.{CsvWriter, _}
import kantan.csv.engine.WriterEngine

/** Provides useful syntax for collections.
  *
  * The sole purpose of this implicit class is to encode collections as CSV into a string through [[asCsv]]:
  *
  * {{{
  *   List(List(1, 2, 3), List(4, 5, 6)).asCsv(',')
  * }}}
  */
final class CsvRowsOps[A](val as: TraversableOnce[A]) extends AnyVal {
  /** Turns the collection into a CSV string.
    *
    * @param sep character used to separate columns.
    * @param header optional header row.
    */
  def asCsv(sep: Char, header: String*)(implicit ea: RowEncoder[A], e: WriterEngine): String = {
    val out = new StringWriter()
    CsvWriter(out, sep, header:_*).write(as).close()
    out.toString
  }
}

trait ToCsvRowsOps {
  implicit def toCsvRowsOps[A](as: TraversableOnce[A]): CsvRowsOps[A] = new CsvRowsOps(as)
}

object csvRows extends ToCsvRowsOps
