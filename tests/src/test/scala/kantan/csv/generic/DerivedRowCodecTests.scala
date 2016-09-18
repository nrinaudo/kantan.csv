/*
 * Copyright 2016 Nicolas Rinaudo
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

package kantan.csv.generic

import arbitrary._
import kantan.codecs.shapeless.laws._
import kantan.csv.laws.discipline.RowCodecTests
import kantan.csv.laws.LegalRow
import kantan.csv.laws.discipline.RowCodecTests
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DerivedRowCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case class Simple(i: Int)
  case class Complex(i: Int, b: Boolean, c: Option[Float])

  implicit val arbLegal: Arbitrary[LegalRow[Or[Complex, Simple]]] =
    arbLegalValue((o: Or[Complex, Simple]) ⇒ o match {
      case Left(Complex(i, b, c)) ⇒ Seq(i.toString, b.toString, c.fold("")(_.toString))
      case Right(Simple(i))       ⇒ Seq(i.toString)
    })



  checkAll("RowCodec[Or[Complex, Simple]]", RowCodecTests[Or[Complex, Simple]].codec[Byte, Float])
}
