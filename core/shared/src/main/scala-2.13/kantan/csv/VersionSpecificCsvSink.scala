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

import kantan.csv.engine.WriterEngine

trait VersionSpecificCsvSink[-S] { self: CsvSink[S] =>
  @deprecated("use write(S, TraversableOnce[A], CsvConfiguration) instead", "0.1.18")
  def write[A: HeaderEncoder](s: S, rows: IterableOnce[A], sep: Char, header: String*)(implicit
    e: WriterEngine
  ): Unit =
    write(s, rows, rfc.withCellSeparator(sep).withHeader(header: _*))

  /** Writes the specified collections directly in the specifie `S`.
    *
    * @param s
    *   where to write the CSV data.
    * @param rows
    *   CSV data to encode and serialize.
    * @param conf
    *   CSV writing behaviour.
    */
  def write[A: HeaderEncoder](s: S, rows: IterableOnce[A], conf: CsvConfiguration)(implicit e: WriterEngine): Unit =
    writer(s, conf).write(rows).close()
}
