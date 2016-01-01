package tabulate.generic

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.IllegalValue
import tabulate.laws.discipline.CellCodecTests
import codecs._

class DerivedCellCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case object Bar extends Foo
  case class Baz(i: Int) extends Foo
  sealed trait Foo

  // TODO: most of this should be possible to derive automatically through shapeless.
  implicit val arbBar: Arbitrary[Bar.type] = Arbitrary(Gen.const(Bar))
  implicit val arbBaz: Arbitrary[Baz] = Arbitrary(Arbitrary.arbitrary[Int].map(Baz.apply))
  implicit val arbFoo: Arbitrary[Foo] = Arbitrary(Gen.oneOf(arbBar.arbitrary, arbBaz.arbitrary))
  implicit val arbIllegalBar: Arbitrary[IllegalValue[Bar.type]] =
    Arbitrary(Arbitrary.arbitrary[Boolean].map(i => IllegalValue(i.toString)))
  implicit val arbIllegalFoo: Arbitrary[IllegalValue[Foo]] =
      Arbitrary(Arbitrary.arbitrary[Boolean].map(i => IllegalValue(i.toString)))

  checkAll("Bar", CellCodecTests[Bar.type].cellCodec[Byte, Float])
  checkAll("Foo", CellCodecTests[Foo].cellCodec[Byte, String])
}
