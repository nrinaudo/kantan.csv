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

package kantan.csv.laws

import kantan.csv.engine.WriterEngine
import kantan.csv.ops._

trait WriterEngineLaws {
  implicit def engine: WriterEngine

  def roundTrip(csv: List[List[Cell]], header: Seq[String]): Boolean =
    csv.asCsv(',', header:_*).unsafeReadCsv[List, List[Cell]](',', header.nonEmpty) == csv

  def noTrailingSeparator(csv: List[List[Cell.NonEscaped]]): Boolean =
    csv.asCsv(',').split("\n").forall(!_.endsWith(","))

  // This test is slightly dodgy, but works: we're assuming that the data is properly serialized (this is checked by
  // roundTrip), an want to make sure that we get the right number of rows. The `trim` bit is to allow for the optional
  // empty row.
  def crlfAsRowSeparator(csv: List[List[Cell.NonEscaped]]): Boolean =
    csv.asCsv(',').trim.split("\r\n").length == csv.length
}

object WriterEngineLaws {
  def apply(e: WriterEngine): WriterEngineLaws = new WriterEngineLaws {
    override implicit val engine = e
  }
}
