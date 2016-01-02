package tabulate

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.CellCodecTests
import tabulate.laws.discipline.arbitrary._

class DoubleTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Double", CellCodecTests[Double].cellCodec[String, Float])
}