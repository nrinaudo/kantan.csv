package tabulate

import org.scalacheck.{Gen, Arbitrary}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.IllegalValue
import tabulate.laws.discipline.{CellCodecTests, RowCodecTests}

class EitherTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegal: Arbitrary[IllegalValue[Either[Int, Boolean]]] =
    Arbitrary(Gen.alphaChar.map(i => IllegalValue(i.toString)))

  checkAll("Either[Int, Boolean]", CellCodecTests[Either[Int, Boolean]].cellCodec[Byte, Float])
  checkAll("Either[(Int, Int, Int), (Boolean, Float)]", RowCodecTests[Either[(Int, Int, Int), (Boolean, Float)]].reversibleRowCodec[Byte, String])
}