package kantan.csv.generic

import kantan.codecs.laws.CodecValue
import kantan.codecs.laws.CodecValue.LegalValue
import kantan.csv.CellDecoder
import kantan.csv.laws.{LegalCell, IllegalCell}
import kantan.csv.laws.discipline.CellCodecTests
import kantan.csv.laws.discipline.arbitrary._
import codecs._
import org.scalacheck.{Gen, Arbitrary}
import org.scalacheck.Shapeless._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DerivedCellCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case object Bar extends Foo
  case class Baz(i: Int) extends Foo
  sealed trait Foo

  implicit val arbLegalBar = Arbitrary(genLegal[String, Bar.type](_ ⇒ ""))
  implicit val arbIllegalBar = Arbitrary(genIllegal[String, Bar.type](_.nonEmpty))

  // TODO: at some point, it would be nice to derive instances of these automatically.
  implicit val arbIllegalFoo: Arbitrary[IllegalCell[Foo]] =
    Arbitrary(Arbitrary.arbitrary[Boolean].map(b ⇒ CodecValue.IllegalValue(b.toString)))
  implicit val arbLegalFoo: Arbitrary[LegalCell[Foo]] = Arbitrary {
    Gen.oneOf(Gen.const(LegalValue("", Bar: Foo)), Arbitrary.arbitrary[Int].map(i ⇒ LegalValue(i.toString, Baz(i): Foo)))
  }


  checkAll("Bar", CellCodecTests[Bar.type].codec[Byte, Float])
  checkAll("Foo", CellCodecTests[Foo].codec[Byte, String])
}
