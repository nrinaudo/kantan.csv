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
package cats

import _root_.cats.Eq
import _root_.cats.data.EitherT
import _root_.cats.instances.all._
import _root_.cats.laws.discipline.{ContravariantTests, MonadErrorTests, SemigroupKTests}
import _root_.cats.laws.discipline.SemigroupalTests.Isomorphisms
import laws.discipline._, arbitrary._, equality._

class DecoderTests extends DisciplineSuite {

  // cats doesn't provide an Eq[Seq] instance, mostly because Seq isn't a very meaningfull type.
  implicit def seqEq[A: Eq]: Eq[Seq[A]] = Eq.by(_.toList)

  // For some reason, these are not derived automatically. I *think* it's to do with the various codecs being type
  // aliases for types with many holes, but this is slightly beyond me.
  implicit val eqCellEitherT: Eq[EitherT[CellDecoder, DecodeError, Int]] = EitherT.catsDataEqForEitherT
  implicit val eqRowEitherT: Eq[EitherT[RowDecoder, DecodeError, Int]]   = EitherT.catsDataEqForEitherT
  implicit val cellIso: Isomorphisms[CellDecoder]                        = Isomorphisms.invariant
  implicit val rowIso: Isomorphisms[RowDecoder]                          = Isomorphisms.invariant

  checkAll("CellDecoder", SemigroupKTests[CellDecoder].semigroupK[Int])
  checkAll("CellDecoder", MonadErrorTests[CellDecoder, DecodeError].monadError[Int, Int, Int])

  checkAll("RowDecoder", SemigroupKTests[RowDecoder].semigroupK[Int])
  checkAll("RowDecoder", MonadErrorTests[RowDecoder, DecodeError].monadError[Int, Int, Int])

  checkAll("CellEncoder", ContravariantTests[CellEncoder].contravariant[Int, Int, Int])
  checkAll("RowEncoder", ContravariantTests[RowEncoder].contravariant[Int, Int, Int])
}
