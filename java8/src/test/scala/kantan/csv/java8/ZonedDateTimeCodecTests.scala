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
package java8

import arbitrary._
import java.time.ZonedDateTime
import laws.discipline._

class ZonedDateTimeCodecTests extends DisciplineSuite {
  checkAll("CellEncoder[ZonedDateTime]", SerializableTests[CellEncoder[ZonedDateTime]].serializable)
  checkAll("CellDecoder[ZonedDateTime]", SerializableTests[CellDecoder[ZonedDateTime]].serializable)

  checkAll("RowEncoder[ZonedDateTime]", SerializableTests[RowEncoder[ZonedDateTime]].serializable)
  checkAll("RowDecoder[ZonedDateTime]", SerializableTests[RowDecoder[ZonedDateTime]].serializable)

  checkAll("CellCodec[ZonedDateTime]", CellCodecTests[ZonedDateTime].codec[String, Float])
  checkAll("RowCodec[ZonedDateTime]", RowCodecTests[ZonedDateTime].codec[String, Float])
}
