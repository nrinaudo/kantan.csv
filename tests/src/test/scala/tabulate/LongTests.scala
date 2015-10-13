package tabulate

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.CellCodecTests

class LongTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Long", CellCodecTests[Long].cellCodec[String, Float])
}