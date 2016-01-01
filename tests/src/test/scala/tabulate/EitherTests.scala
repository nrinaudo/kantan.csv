package tabulate

import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.IllegalValue
import tabulate.laws.discipline.{CellCodecTests, RowCodecTests}

class EitherTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbIllegal: Arbitrary[IllegalValue[Either[Int, Char]]] =
    Arbitrary(Arbitrary.arbitrary[Boolean].map(i => IllegalValue(i.toString)))

  checkAll("Either[Int, Char]", CellCodecTests[Either[Int, Char]].cellCodec[Byte, Float])
  checkAll("Either[(Int, Int, Int), (Boolean, Float)]", RowCodecTests[Either[(Int, Int, Int), (Boolean, Float)]].reversibleRowCodec[Byte, String])
}