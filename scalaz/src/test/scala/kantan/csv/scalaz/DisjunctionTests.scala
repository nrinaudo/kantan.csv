package kantan.csv.scalaz

import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import kantan.csv.laws.{IllegalCell, IllegalRow}
import codecs._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

import _root_.scalaz.\/
import _root_.scalaz.scalacheck.ScalazArbitrary._

class DisjunctionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegalCell: Arbitrary[IllegalCell[Int \/ Boolean]] =
    illegal(Gen.alphaChar.map(_.toString))

  implicit val arbIllegalRow: Arbitrary[IllegalRow[(Int, Int, Int) \/ (Boolean, Float)]] =
    illegal(Gen.alphaChar.map(s ⇒ Seq(s.toString)))

  checkAll("Int \\/ Boolean", CellCodecTests[Int \/ Boolean].cellCodec[Byte, Float])
  checkAll("(Int, Int, Int) \\/ (Boolean, Float)", RowCodecTests[(Int, Int, Int) \/ (Boolean, Float)].rowCodec[Byte, String])
}