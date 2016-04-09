package kantan.csv

import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class IntCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[Int]", CellCodecTests[Int].codec[String, Float])
  checkAll("RowCodec[Int]", RowCodecTests[Int].codec[String, Float])
}
