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
import kantan.csv.CsvSink
import kantan.csv.HeaderEncoder
import kantan.csv.engine.WriterEngine
import kantan.csv.rfc

trait VersionSpecificCsvSinkOps[A] { self: CsvSinkOps[A] =>
  @deprecated("use writeCsv(rows, CsvConfiguration) instead", "0.1.18")
  def writeCsv[B: HeaderEncoder](rows: IterableOnce[B], sep: Char, header: String*)(implicit
    e: WriterEngine,
    sa: CsvSink[A]
  ): Unit =
    writeCsv(rows, rfc.withCellSeparator(sep).withHeader(header: _*))

  def writeCsv[B: HeaderEncoder](
    rows: IterableOnce[B],
    conf: CsvConfiguration
  )(implicit e: WriterEngine, sa: CsvSink[A]): Unit =
    CsvSink[A].write(a, rows, conf)
}
