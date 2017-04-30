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

package kantan.csv

import java.util.UUID
import kantan.codecs.laws.discipline.SerializableTests
import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class UUIDCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellEncoder[UUID]", SerializableTests[CellEncoder[UUID]].serializable)
  checkAll("CellDecoder[UUID]", SerializableTests[CellDecoder[UUID]].serializable)

  checkAll("RowEncoder[UUID]", SerializableTests[RowEncoder[UUID]].serializable)
  checkAll("RowDecoder[UUID]", SerializableTests[RowDecoder[UUID]].serializable)

  checkAll("CellCodec[UUID]", CellCodecTests[UUID].codec[String, Float])
  checkAll("RowCodec[UUID]", RowCodecTests[UUID].codec[String, Float])
}
