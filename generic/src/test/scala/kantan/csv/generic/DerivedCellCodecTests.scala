package kantan.csv.generic

import kantan.codecs.laws.CodecValue
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.codecs.laws.discipline.GenCodecValue
import kantan.csv.laws._
import kantan.csv.laws.discipline.CellCodecTests
import kantan.csv.laws.discipline.arbitrary._
import org.scalacheck._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.Shapeless._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

// TODO: clean the Arbitrary instances, they're a disgrace.

class DerivedCellCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case object Bar extends Foo
  case class Baz(i: Int) extends Foo
  sealed trait Foo

  implicit val arbLegalBar: Arbitrary[LegalCell[Bar.type]] = Arbitrary(Gen.const(LegalValue("", Bar)))
  implicit val arbIllegalBar: Arbitrary[IllegalCell[Bar.type]] = Arbitrary {
    for(s ← Arbitrary.arbitrary[String].suchThat(_.nonEmpty)) yield IllegalValue(s)
  }

  // TODO: at some point, it would be nice to derive instances of these automatically.
  implicit val arbIllegalFoo: Arbitrary[IllegalCell[Foo]] =
    Arbitrary(Arbitrary.arbitrary[Boolean].map(b ⇒ CodecValue.IllegalValue(b.toString)))
  implicit val arbLegalFoo: Arbitrary[LegalCell[Foo]] = Arbitrary {
    Gen.oneOf(Gen.const(LegalValue("", Bar: Foo)), arb[Int].map(i ⇒ LegalValue(i.toString, Baz(i): Foo)))
  }


  checkAll("Bar", CellCodecTests[Bar.type].codec[Byte, Float])
  checkAll("Foo", CellCodecTests[Foo].codec[Byte, String])
}
