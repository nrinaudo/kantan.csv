package tabulate

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.CellCodecTests

class StringTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("String", CellCodecTests[String].cellCodec[Int, Float])
}