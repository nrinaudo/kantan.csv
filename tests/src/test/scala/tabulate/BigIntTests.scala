package tabulate

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.CellCodecTests

class BigIntTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("BigInt", CellCodecTests[BigInt].cellCodec[String, Float])
}