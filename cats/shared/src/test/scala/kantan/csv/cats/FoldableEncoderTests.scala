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

package kantan.csv.cats

import cats.instances.list._
import kantan.csv.RowEncoder
import kantan.csv.cats.arbitrary._
import kantan.csv.laws.discipline.DisciplineSuite
import kantan.csv.laws.discipline.RowEncoderTests
import org.scalacheck.Arbitrary
import org.scalacheck.Gen

class FoldableEncoderTests extends DisciplineSuite {

  implicit val arb: Arbitrary[List[Int]] = Arbitrary(Gen.nonEmptyListOf(Arbitrary.arbitrary[Int]))

  // We need this to prevent the standard List RowDecoder from being used. Happy to change this as soon as someone
  // shows me a Foldable type that doesn't already have a RowDecoder instance.
  implicit val encoder: RowEncoder[List[Int]] = foldableRowEncoder[List, Int]

  checkAll("Foldable[Int]", RowEncoderTests[List[Int]].encoder[List[Byte], List[Float]])

}
