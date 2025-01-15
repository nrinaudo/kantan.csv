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

package kantan.csv.refined

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import kantan.csv.CellDecoder
import kantan.csv.CellEncoder
import kantan.csv.RowDecoder
import kantan.csv.RowEncoder
import kantan.csv.laws.discipline.DisciplineSuite
import kantan.csv.laws.discipline.SerializableTests

class SerialisationTests extends DisciplineSuite {

  checkAll("CellEncoder[Int Refined Positive]", SerializableTests[CellEncoder[Int Refined Positive]].serializable)
  checkAll("CellDecoder[Int Refined Positive]", SerializableTests[CellDecoder[Int Refined Positive]].serializable)

  checkAll("RowEncoder[Int Refined Positive]", SerializableTests[RowEncoder[Int Refined Positive]].serializable)
  checkAll("RowDecoder[Int Refined Positive]", SerializableTests[RowDecoder[Int Refined Positive]].serializable)

}
