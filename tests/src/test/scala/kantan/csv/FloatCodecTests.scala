package kantan.csv

import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class FloatCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[Float]", CellCodecTests[Float].codec[String, Float])
  checkAll("RowCodec[Float]", RowCodecTests[Float].codec[String, Float])
}
