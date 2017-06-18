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

import kantan.codecs.laws.discipline.SerializableTests
import kantan.csv.laws.discipline.RowCodecTests
import kantan.csv.laws.discipline.arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class VectorCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[Vector[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Vector, Int](Arbitrary.arbitrary[Int]))

  checkAll("RowEncoder[Vector[Int]]", SerializableTests[RowEncoder[Vector[Int]]].serializable)
  checkAll("RowDecoder[Vector[Int]]", SerializableTests[RowDecoder[Vector[Int]]].serializable)

  checkAll("RowCodec[Vector[Int]]", RowCodecTests[Vector[Int]].codec[List[String], List[Float]])
}
