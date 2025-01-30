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
import kantan.csv.laws.discipline.CellCodecTests
import kantan.csv.laws.discipline.DisciplineSuite
import kantan.csv.laws.discipline.RowCodecTests
import kantan.csv.refined.arbitrary._

class RefinedCodecTests extends DisciplineSuite {

  checkAll("CellCodec[Int Refined Positive]", CellCodecTests[Int Refined Positive].codec[String, Float])
  checkAll("RowCodec[Int Refined Positive]", RowCodecTests[Int Refined Positive].codec[String, Float])

}
