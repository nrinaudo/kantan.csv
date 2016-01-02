package tabulate

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.{CellCodecTests, RowCodecTests}
import tabulate.laws.{IllegalRow, IllegalCell, IllegalValue}

class EitherTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegalCell: Arbitrary[IllegalCell[Either[Int, Boolean]]] =
    IllegalValue.arbitrary(Gen.alphaChar.map(_.toString))

  implicit val arbIllegalRow: Arbitrary[IllegalRow[Either[(Int, Int, Int), (Boolean, Float)]]] =
    IllegalValue.arbitrary(Gen.alphaChar.map(s => Seq(s.toString)))

  checkAll("Either[Int, Boolean]", CellCodecTests[Either[Int, Boolean]].cellCodec[Byte, Float])
  checkAll("Either[(Int, Int, Int), (Boolean, Float)]", RowCodecTests[Either[(Int, Int, Int), (Boolean, Float)]].rowCodec[Byte, String])
}