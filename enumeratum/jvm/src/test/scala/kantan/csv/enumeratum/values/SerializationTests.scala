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

package kantan.csv.enumeratum.values

import kantan.codecs.enumeratum.laws.discipline._
import kantan.csv.CellDecoder
import kantan.csv.CellEncoder
import kantan.csv.RowDecoder
import kantan.csv.RowEncoder

class SerializationTests extends DisciplineSuite {

  checkAll("CellEncoder[EnumeratedByte]", SerializableTests[CellEncoder[EnumeratedByte]].serializable)
  checkAll("CellDecoder[EnumeratedByte]", SerializableTests[CellDecoder[EnumeratedByte]].serializable)
  checkAll("RowEncoder[EnumeratedByte]", SerializableTests[RowEncoder[EnumeratedByte]].serializable)
  checkAll("RowDecoder[EnumeratedByte]", SerializableTests[RowDecoder[EnumeratedByte]].serializable)

  checkAll("CellEncoder[EnumeratedChar]", SerializableTests[CellEncoder[EnumeratedChar]].serializable)
  checkAll("CellDecoder[EnumeratedChar]", SerializableTests[CellDecoder[EnumeratedChar]].serializable)
  checkAll("RowEncoder[EnumeratedChar]", SerializableTests[RowEncoder[EnumeratedChar]].serializable)
  checkAll("RowDecoder[EnumeratedChar]", SerializableTests[RowDecoder[EnumeratedChar]].serializable)

  checkAll("CellEncoder[EnumeratedInt]", SerializableTests[CellEncoder[EnumeratedInt]].serializable)
  checkAll("CellDecoder[EnumeratedInt]", SerializableTests[CellDecoder[EnumeratedInt]].serializable)
  checkAll("RowEncoder[EnumeratedInt]", SerializableTests[RowEncoder[EnumeratedInt]].serializable)
  checkAll("RowDecoder[EnumeratedInt]", SerializableTests[RowDecoder[EnumeratedInt]].serializable)

  checkAll("CellEncoder[EnumeratedLong]", SerializableTests[CellEncoder[EnumeratedLong]].serializable)
  checkAll("CellDecoder[EnumeratedLong]", SerializableTests[CellDecoder[EnumeratedLong]].serializable)
  checkAll("RowEncoder[EnumeratedLong]", SerializableTests[RowEncoder[EnumeratedLong]].serializable)
  checkAll("RowDecoder[EnumeratedLong]", SerializableTests[RowDecoder[EnumeratedLong]].serializable)

  checkAll("CellEncoder[EnumeratedShort]", SerializableTests[CellEncoder[EnumeratedShort]].serializable)
  checkAll("CellDecoder[EnumeratedShort]", SerializableTests[CellDecoder[EnumeratedShort]].serializable)
  checkAll("RowEncoder[EnumeratedShort]", SerializableTests[RowEncoder[EnumeratedShort]].serializable)
  checkAll("RowDecoder[EnumeratedShort]", SerializableTests[RowDecoder[EnumeratedShort]].serializable)

  checkAll("CellEncoder[EnumeratedString]", SerializableTests[CellEncoder[EnumeratedString]].serializable)
  checkAll("CellDecoder[EnumeratedString]", SerializableTests[CellDecoder[EnumeratedString]].serializable)
  checkAll("RowEncoder[EnumeratedString]", SerializableTests[RowEncoder[EnumeratedString]].serializable)
  checkAll("RowDecoder[EnumeratedString]", SerializableTests[RowDecoder[EnumeratedString]].serializable)

}
