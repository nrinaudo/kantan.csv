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

package kantan.csv.engine.jackson

import kantan.csv.laws.Cell
import kantan.csv.laws.discipline.DisciplineSuite
import kantan.csv.laws.discipline.WriterEngineTests
import kantan.csv.ops._
import kantan.csv.rfc

class JacksonWriterTests extends DisciplineSuite {
  checkAll("JacksonWriter", WriterEngineTests(jacksonCsvWriterEngine).writerEngine)

  test("Trailing cells composed of a single \\n are properly encoded and decoded") {
    val csv: List[List[Cell]] = List(List(Cell.NonEscaped("a"), Cell.Escaped("\n")))

    val decoded = csv.asCsv(rfc).unsafeReadCsv[List, List[Cell]](rfc)

    csv should be(decoded)
  }
}
