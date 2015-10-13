package tabulate

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.{CellCodecTests, RowCodecTests}

class EitherTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Either[Int, Boolean]", CellCodecTests[Either[Int, Boolean]].cellCodec[Byte, Float])
  checkAll("Either[(Int, Int, Int), (Boolean, Float)]", RowCodecTests[Either[(Int, Int, Int), (Boolean, Float)]].rowCodec[Byte, String])
}