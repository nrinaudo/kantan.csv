package kantan.csv.generic

import kantan.codecs.laws.CodecValue
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.csv.laws._
import kantan.csv.laws.discipline.RowCodecTests
import kantan.csv.laws.discipline.arbitrary._
import codecs._
import org.scalacheck.{Gen, Arbitrary}
import org.scalacheck.Arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DerivedRowCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case object Bar extends Foo
  case class Baz(i: Int, b: Boolean) extends Foo
  sealed trait Foo

  case class Wrapper[A](value: A)

  implicit val arbLegalBar: Arbitrary[LegalRow[Bar.type]] = Arbitrary(Gen.const(LegalValue(Seq.empty[String], Bar)))
  implicit val arbIllegalBar: Arbitrary[IllegalRow[Bar.type]] = Arbitrary {
    for(s ← Gen.nonEmptyListOf(Arbitrary.arbitrary[String])) yield IllegalValue(s)
  }

  implicit def arbLegalWrapper[A](implicit aa: Arbitrary[LegalRow[A]]): Arbitrary[LegalRow[Wrapper[A]]] =
    Arbitrary(aa.arbitrary.map(va ⇒ va.mapDecoded(Wrapper.apply)))
  implicit def arbIllegalWrapper[A](implicit aa: Arbitrary[IllegalRow[A]]): Arbitrary[IllegalRow[Wrapper[A]]] =
      Arbitrary(aa.arbitrary.map(va ⇒ va.mapDecoded(Wrapper.apply)))

  implicit val arbLegalFoo: Arbitrary[LegalRow[Foo]] = Arbitrary {
    Gen.oneOf(Gen.const(LegalValue(Seq.empty[String], Bar: Foo)), for {
      i ← Arbitrary.arbitrary[Int]
      b ← Arbitrary.arbitrary[Boolean]
    } yield LegalValue(Seq(i.toString, b.toString), Baz(i, b): Foo))
  }
  implicit val arbIllegalFoo: Arbitrary[IllegalRow[Foo]] =
    Arbitrary(Arbitrary.arbitrary[Boolean].map(s ⇒ CodecValue.IllegalValue(Seq(s.toString))))

  checkAll("Wrapper[(Int, Int)]", RowCodecTests[Wrapper[(Int, Int)]].codec[Byte, Float])
  checkAll("Wrapper[Foo]", RowCodecTests[Wrapper[Foo]].codec[Byte, Float])
  checkAll("Bar", RowCodecTests[Bar.type].codec[Byte, Float])
  checkAll("Foo", RowCodecTests[Foo].codec[Byte, String])
}

