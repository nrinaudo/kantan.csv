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

package kantan.csv.java8

import java.time.LocalTime
import kantan.csv.{CellDecoder, CellEncoder, RowDecoder, RowEncoder}
import kantan.csv.java8.arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, DisciplineSuite, RowCodecTests, SerializableTests}

class LocalTimeCodecTests extends DisciplineSuite {
  checkAll("CellEncoder[LocalTime]", SerializableTests[CellEncoder[LocalTime]].serializable)
  checkAll("CellDecoder[LocalTime]", SerializableTests[CellDecoder[LocalTime]].serializable)

  checkAll("RowEncoder[LocalTime]", SerializableTests[RowEncoder[LocalTime]].serializable)
  checkAll("RowDecoder[LocalTime]", SerializableTests[RowDecoder[LocalTime]].serializable)

  checkAll("CellCodec[LocalTime]", CellCodecTests[LocalTime].codec[String, Float])
  checkAll("RowCodec[LocalTime]", RowCodecTests[LocalTime].codec[String, Float])
}
