package kantan.csv

import kantan.csv.laws.discipline.SafeCellCodecTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class StringTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("String", SafeCellCodecTests[String].safeCellCodec[Int, Float])
}