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
import java.time.OffsetDateTime
import laws.discipline._

class OffsetDateTimeCodecTests extends DisciplineSuite {
  checkAll("CellEncoder[OffsetDateTime]", SerializableTests[CellEncoder[OffsetDateTime]].serializable)
  checkAll("CellDecoder[OffsetDateTime]", SerializableTests[CellDecoder[OffsetDateTime]].serializable)

  checkAll("RowEncoder[OffsetDateTime]", SerializableTests[RowEncoder[OffsetDateTime]].serializable)
  checkAll("RowDecoder[OffsetDateTime]", SerializableTests[RowDecoder[OffsetDateTime]].serializable)

  checkAll("CellCodec[OffsetDateTime]", CellCodecTests[OffsetDateTime].codec[String, Float])
  checkAll("RowCodec[OffsetDateTime]", RowCodecTests[OffsetDateTime].codec[String, Float])
}
