package tabulate.generic

import org.scalacheck.{Gen, Arbitrary}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.IllegalCell
import tabulate.laws.discipline.CellCodecTests
import org.scalacheck.Shapeless._
import tabulate.laws.discipline.CellCodecTests
import tabulate.laws.discipline.arbitrary._
import codecs._

class DerivedCellCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  case object Bar extends Foo
  case class Baz(i: Int) extends Foo
  sealed trait Foo

  implicit val arbIllegalFoo: Arbitrary[IllegalCell[Foo]] =
    illegal(Arbitrary.arbitrary[Boolean].map(_.toString))

  checkAll("Bar", CellCodecTests[Bar.type].cellCodec[Byte, Float])
  checkAll("Foo", CellCodecTests[Foo].cellCodec[Byte, String])
}
