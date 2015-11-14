package tabulate

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.{RowCodecTests, CellCodecTests}

class OptionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Option[Int]", CellCodecTests[Option[Int]].cellCodec[String, Float])
  checkAll("Option[(Int, Int, Int)]", RowCodecTests[Option[(Int, Int, Int)]].rowCodec[Byte, String])
}