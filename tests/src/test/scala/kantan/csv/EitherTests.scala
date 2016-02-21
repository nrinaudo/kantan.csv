package kantan.csv

import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class EitherTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Either[Int, Boolean]", CellCodecTests[Either[Int, Boolean]].codec[Byte, Float])
  checkAll("Either[(Int, Int, Int), (Boolean, Float)]", RowCodecTests[Either[(Int, Int, Int), (Boolean, Float)]].codec[Byte, String])
}