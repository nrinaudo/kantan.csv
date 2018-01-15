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
package joda.time

import arbitrary._
import laws.discipline._
import org.joda.time.LocalTime

class LocalTimeCodecTests extends DisciplineSuite {

  checkAll("CellCodec[LocalTime]", CellCodecTests[LocalTime].codec[String, Float])

  checkAll("CellDecoder[LocalTime]", CellDecoderTests[LocalTime].decoder[String, Float])
  checkAll("CellDecoder[LocalTime]", SerializableTests[CellDecoder[LocalTime]].serializable)

  checkAll("CellEncoder[LocalTime]", CellEncoderTests[LocalTime].encoder[String, Float])
  checkAll("CellEncoder[LocalTime]", SerializableTests[CellEncoder[LocalTime]].serializable)

}
