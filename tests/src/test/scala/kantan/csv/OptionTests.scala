package kantan.csv

import kantan.csv.laws.IllegalRow
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class OptionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegalRow: Arbitrary[IllegalRow[Option[(Int, Int, Int)]]] =
      illegal(Gen.alphaChar.map(s ⇒ Seq(s.toString)))

  checkAll("Option[Int]", CellCodecTests[Option[Int]].cellCodec[String, Float])
  checkAll("Option[(Int, Int, Int)]", RowCodecTests[Option[(Int, Int, Int)]].rowCodec[Byte, String])
}