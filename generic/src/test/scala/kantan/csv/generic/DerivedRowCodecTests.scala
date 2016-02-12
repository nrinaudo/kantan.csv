package kantan.csv.generic

import kantan.csv.laws.IllegalRow
import kantan.csv.laws.discipline.RowCodecTests
import kantan.csv.laws.discipline.arbitrary._
import codecs._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalacheck.Shapeless._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DerivedRowCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case object Bar extends Foo
  case class Baz(i: Int, b: Boolean) extends Foo
  sealed trait Foo

  case class Wrapper[A](value: A)

  implicit val arbIllegalFoo: Arbitrary[IllegalRow[Foo]] =
    illegal(Arbitrary.arbitrary[Boolean].map(s ⇒ Seq(s.toString)))

  checkAll("Wrapper[(Int, Int)]", RowCodecTests[Wrapper[(Int, Int)]].rowCodec[Byte, Float])
  checkAll("Wrapper[Foo]", RowCodecTests[Wrapper[Foo]].rowCodec[Byte, Float])
  checkAll("Bar", RowCodecTests[Bar.type].rowCodec[Byte, Float])
  checkAll("Foo", RowCodecTests[Foo].rowCodec[Byte, String])
}

