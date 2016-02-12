package kantan.csv.cats

import org.scalacheck.{Gen, Arbitrary}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import kantan.csv.laws._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import codecs._
import _root_.cats.data.Xor
import _root_.cats.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.arbitrary._

class XorTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegalCell: Arbitrary[IllegalCell[Xor[Int, Boolean]]] =
    illegal(Gen.alphaChar.map(_.toString))

  implicit val arbIllegalRow: Arbitrary[IllegalRow[Xor[(Int, Int, Int), (Boolean, Float)]]] =
    illegal(Gen.alphaChar.map(s ⇒ Seq(s.toString)))

  checkAll("Xor[Int, Boolean]", CellCodecTests[Xor[Int, Boolean]].cellCodec[Byte, Float])
  checkAll("Xor[(Int, Int, Int), (Boolean, Float)]", RowCodecTests[Xor[(Int, Int, Int), (Boolean, Float)]].rowCodec[Byte, String])
}