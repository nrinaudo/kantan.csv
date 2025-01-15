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

import cats.Eq
import cats.data.EitherT
import cats.instances.all._
import cats.laws.discipline.ContravariantTests
import cats.laws.discipline.MonadErrorTests
import cats.laws.discipline.SemigroupKTests
import cats.laws.discipline.SemigroupalTests.Isomorphisms
import kantan.csv.CellDecoder
import kantan.csv.CellEncoder
import kantan.csv.DecodeError
import kantan.csv.cats.arbitrary._
import kantan.csv.cats.equality._
import kantan.csv.laws.discipline.DisciplineSuite

class CellCodecInstancesTests extends DisciplineSuite {

  implicitly[Eq[Int]]

  // For some reason, these are not derived automatically. I *think* it's to do with the various codecs being type
  // aliases for types with many holes, but this is slightly beyond me.
  implicit val eqCellEitherT: Eq[EitherT[CellDecoder, DecodeError, Int]] = EitherT.catsDataEqForEitherT
  implicit val cellIso: Isomorphisms[CellDecoder]                        = Isomorphisms.invariant

  checkAll("CellDecoder", SemigroupKTests[CellDecoder].semigroupK[Int])
  checkAll("CellDecoder", MonadErrorTests[CellDecoder, DecodeError].monadError[Int, Int, Int])
  checkAll("CellEncoder", ContravariantTests[CellEncoder].contravariant[Int, Int, Int])

}
