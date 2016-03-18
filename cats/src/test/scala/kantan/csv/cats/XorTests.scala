package kantan.csv.cats

import _root_.cats.data.Xor
import arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class XorTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Xor[Int, Boolean]", CellCodecTests[Xor[Int, Boolean]].codec[Byte, Float])
  checkAll("Xor[(Int, Int, Int), (Boolean, Float)]", RowCodecTests[Xor[(Int, Int, Int), (Boolean, Float)]].codec[Byte, String])
}