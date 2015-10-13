package tabulate

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.CellCodecTests

class BooleanTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Boolean", CellCodecTests[Boolean].cellCodec[String, Float])
}