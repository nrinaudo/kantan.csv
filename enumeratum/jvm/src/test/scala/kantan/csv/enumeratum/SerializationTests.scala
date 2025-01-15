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

package kantan.csv.enumeratum

import kantan.codecs.enumeratum.laws.discipline.Enumerated
import kantan.csv.CellDecoder
import kantan.csv.CellEncoder
import kantan.csv.RowDecoder
import kantan.csv.RowEncoder
import kantan.csv.laws.discipline.DisciplineSuite
import kantan.csv.laws.discipline.SerializableTests

class SerializationTests extends DisciplineSuite {

  checkAll("CellEncoder[Enumerated]", SerializableTests[CellEncoder[Enumerated]].serializable)
  checkAll("CellDecoder[Enumerated]", SerializableTests[CellDecoder[Enumerated]].serializable)

  checkAll("RowEncoder[Enumerated]", SerializableTests[RowEncoder[Enumerated]].serializable)
  checkAll("RowDecoder[Enumerated]", SerializableTests[RowDecoder[Enumerated]].serializable)

}
