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

package kantan.csv.scalaz

import kantan.codecs.scalaz.laws.discipline.ScalazDisciplineSuite
import kantan.csv.DecodeError
import kantan.csv.RowDecoder
import kantan.csv.RowEncoder
import kantan.csv.scalaz.arbitrary._
import kantan.csv.scalaz.equality._
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.contravariant
import scalaz.scalacheck.ScalazProperties.monadError
import scalaz.scalacheck.ScalazProperties.plus
import scalaz.std.anyVal._
import scalaz.std.list._
import scalaz.std.string._

class RowCodecInstancesTests extends ScalazDisciplineSuite {

  // Limits the size of rows to 10 - using the default size makes these tests prohibitively long in some contexts
  // (in particular, travis will timeout on the scala.js execution of these tests).
  implicit def arbSeq[A: Arbitrary]: Arbitrary[Seq[A]] =
    Arbitrary(Gen.listOfN(10, implicitly[Arbitrary[A]].arbitrary))

  // scalaz doesn't provide an Eq[Seq] instance, mostly because Seq isn't a very meaningfull type.
  implicit def seqEq[A: Equal]: Equal[Seq[A]] =
    Equal[List[A]].contramap(_.toList)

  checkAll("RowDecoder", monadError.laws[RowDecoder, DecodeError])
  checkAll("RowDecoder", plus.laws[RowDecoder])
  checkAll("RowEncoder", contravariant.laws[RowEncoder])

}
