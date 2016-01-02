package tabulate

import org.scalacheck.{Gen, Arbitrary}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws._
import tabulate.laws.discipline.{CellCodecTests, RowCodecTests}

class OptionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegalRow: Arbitrary[IllegalRow[Option[(Int, Int, Int)]]] =
      IllegalValue.arbitrary(Gen.alphaChar.map(s => Seq(s.toString)))

  checkAll("Option[Int]", CellCodecTests[Option[Int]].cellCodec[String, Float])
  checkAll("Option[(Int, Int, Int)]", RowCodecTests[Option[(Int, Int, Int)]].rowCodec[Byte, String])
}