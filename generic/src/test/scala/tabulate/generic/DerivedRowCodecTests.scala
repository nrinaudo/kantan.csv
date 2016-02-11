package tabulate.generic

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws._
import tabulate.laws.discipline.RowCodecTests
import org.scalacheck.Shapeless._
import codecs._
import tabulate.laws.discipline.arbitrary._

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

