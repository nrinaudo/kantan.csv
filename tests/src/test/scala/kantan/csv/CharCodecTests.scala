package kantan.csv

import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class CharCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[Char]", CellCodecTests[Char].codec[String, Float])
  checkAll("RowCodec[Char]", RowCodecTests[Char].codec[String, Float])
}
