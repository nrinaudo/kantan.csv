package kantan.csv

import kantan.csv.laws.discipline.CellCodecTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BooleanTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[Boolean]", CellCodecTests[Boolean].codec[String, Float])
}
