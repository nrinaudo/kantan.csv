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
import kantan.codecs.laws.discipline.SerializableTests
import kantan.csv.{CellDecoder, CellEncoder, RowDecoder, RowEncoder}
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import kantan.csv.refined.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class RefinedCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellEncoder[Int Refined Positive]", SerializableTests[CellEncoder[Int Refined Positive]].serializable)
  checkAll("CellDecoder[Int Refined Positive]", SerializableTests[CellDecoder[Int Refined Positive]].serializable)

  checkAll("RowEncoder[Int Refined Positive]", SerializableTests[RowEncoder[Int Refined Positive]].serializable)
  checkAll("RowDecoder[Int Refined Positive]", SerializableTests[RowDecoder[Int Refined Positive]].serializable)

  checkAll("CellCodec[Int Refined Positive]", CellCodecTests[Int Refined Positive].codec[String, Float])
  checkAll("RowCodec[Int Refined Positive]", RowCodecTests[Int Refined Positive].codec[String, Float])
}
