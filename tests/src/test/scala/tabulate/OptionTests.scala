package tabulate

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.CellCodecTests

class OptionTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Option[Int]", CellCodecTests[Option[Int]].cellCodec[String, Float])
}