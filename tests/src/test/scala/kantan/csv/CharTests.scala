package kantan.csv

import kantan.csv.laws.discipline.CellCodecTests
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class CharTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Char", CellCodecTests[Char].cellCodec[String, Float])
}