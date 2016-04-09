package kantan.csv

import kantan.csv.laws.discipline._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ByteCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CellCodec[Byte]", CellCodecTests[Byte].codec[String, Float])
  checkAll("RowCodec[Byte]", RowCodecTests[Byte].codec[String, Float])
}
