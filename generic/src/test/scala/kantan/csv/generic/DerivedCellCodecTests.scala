package kantan.csv.generic

import kantan.csv.laws.IllegalCell
import kantan.csv.laws.discipline.CellCodecTests
import kantan.csv.laws.discipline.arbitrary._
import codecs._
import org.scalacheck.Arbitrary
import org.scalacheck.Shapeless._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DerivedCellCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case object Bar extends Foo
  case class Baz(i: Int) extends Foo
  sealed trait Foo

  implicit val arbIllegalFoo: Arbitrary[IllegalCell[Foo]] =
    illegal(Arbitrary.arbitrary[Boolean].map(_.toString))

  checkAll("Bar", CellCodecTests[Bar.type].cellCodec[Byte, Float])
  checkAll("Foo", CellCodecTests[Foo].cellCodec[Byte, String])
}
