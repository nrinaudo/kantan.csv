package tabulate.interop.scalaz

import org.scalacheck.{Gen, Arbitrary}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws._
import tabulate.laws.discipline.{CellCodecTests, RowCodecTests}
import tabulate.laws.discipline.arbitrary._

import codecs._
import _root_.scalaz.\/
import _root_.scalaz.scalacheck.ScalazArbitrary._

class DisjunctionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegalCell: Arbitrary[IllegalCell[Int \/ Boolean]] =
    illegal(Gen.alphaChar.map(_.toString))

  implicit val arbIllegalRow: Arbitrary[IllegalRow[(Int, Int, Int) \/ (Boolean, Float)]] =
    illegal(Gen.alphaChar.map(s => Seq(s.toString)))

  checkAll("Int \\/ Boolean", CellCodecTests[Int \/ Boolean].cellCodec[Byte, Float])
  checkAll("(Int, Int, Int) \\/ (Boolean, Float)", RowCodecTests[(Int, Int, Int) \/ (Boolean, Float)].rowCodec[Byte, String])
}