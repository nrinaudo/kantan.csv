package kantan.csv

import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class DoubleCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[Double]", CellCodecTests[Double].codec[String, Float])
  checkAll("RowCodec[Double]", RowCodecTests[Double].codec[String, Float])
}
