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

import java.time.Instant
import kantan.codecs.laws.discipline.SerializableTests
import kantan.csv.{CellDecoder, CellEncoder, RowDecoder, RowEncoder}
import kantan.csv.java8.arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class InstantCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellEncoder[Instant]", SerializableTests[CellEncoder[Instant]].serializable)
  checkAll("CellDecoder[Instant]", SerializableTests[CellDecoder[Instant]].serializable)

  checkAll("RowEncoder[Instant]", SerializableTests[RowEncoder[Instant]].serializable)
  checkAll("RowDecoder[Instant]", SerializableTests[RowDecoder[Instant]].serializable)

  checkAll("CellCodec[Instant]", CellCodecTests[Instant].codec[String, Float])
  checkAll("RowCodec[Instant]", RowCodecTests[Instant].codec[String, Float])
}
