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

import laws.discipline._
import laws.discipline.arbitrary._

class StringCodecTests extends DisciplineSuite {

  checkAll("CellEncoder[String]", SerializableTests[CellEncoder[String]].serializable)
  checkAll("CellDecoder[String]", SerializableTests[CellDecoder[String]].serializable)

  checkAll("RowEncoder[String]", SerializableTests[RowEncoder[String]].serializable)
  checkAll("RowDecoder[String]", SerializableTests[RowDecoder[String]].serializable)

  checkAll("CellCodec[String]", CellCodecTests[String].bijectiveCodec[Int, Float])

}
