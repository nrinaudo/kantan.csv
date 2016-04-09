package kantan.csv

import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigDecimalCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[BigDecimal]", CellCodecTests[BigDecimal].codec[String, Float])
  checkAll("RowCodec[BigDecimal]", RowCodecTests[BigDecimal].codec[String, Float])
}
