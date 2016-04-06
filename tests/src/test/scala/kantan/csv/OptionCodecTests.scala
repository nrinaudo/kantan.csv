package kantan.csv

import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class OptionCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[Option[Int]]", CellCodecTests[Option[Int]].codec[String, Float])
  checkAll("RowCodec[Option[(Int, Int, Int)]]", RowCodecTests[Option[(Int, Int, Int)]].codec[Byte, String])
}
