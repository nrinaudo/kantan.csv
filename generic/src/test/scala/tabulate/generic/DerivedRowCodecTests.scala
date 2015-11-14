package tabulate.generic

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.RowCodecTests
import codecs._

class DerivedRowCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case object Bar extends Foo
  case class Baz(i: Int, b: Boolean) extends Foo
  sealed trait Foo

  case class Wrapper[A](value: A)

  implicit val arbBar: Arbitrary[Bar.type] = Arbitrary(Gen.const(Bar))
  implicit val arbBaz: Arbitrary[Baz] = Arbitrary(for(i <- arbitrary[Int]; b <- arbitrary[Boolean]) yield Baz(i, b))
  implicit val arbFoo: Arbitrary[Foo] = Arbitrary(Gen.oneOf(arbBar.arbitrary, arbBaz.arbitrary))
  implicit def arbWrap[A: Arbitrary]: Arbitrary[Wrapper[A]] = Arbitrary(Arbitrary.arbitrary[A].map(a => Wrapper(a)))

  checkAll("Wrapper[(Int, Int)]", RowCodecTests[Wrapper[(Int, Int)]].rowCodec[Byte, Float])
  checkAll("Wrapper[Foo]", RowCodecTests[Wrapper[Foo]].rowCodec[Byte, Float])
  checkAll("Bar", RowCodecTests[Bar.type].rowCodec[Byte, Float])
  checkAll("Foo", RowCodecTests[Foo].rowCodec[Byte, String])
}

