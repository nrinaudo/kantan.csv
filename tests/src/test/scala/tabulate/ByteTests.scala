package tabulate

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.CellCodecTests
import tabulate.laws.discipline.arbitrary._

class ByteTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Byte", CellCodecTests[Byte].cellCodec[String, Float])
}