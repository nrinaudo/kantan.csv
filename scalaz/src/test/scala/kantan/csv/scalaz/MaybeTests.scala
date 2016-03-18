package kantan.csv.scalaz

import arbitrary._
import kantan.csv.laws.discipline.{CellCodecTests, RowCodecTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import scalaz.Maybe

class MaybeTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Maybe[Int]", CellCodecTests[Maybe[Int]].codec[String, Float])
  checkAll("Maybe[(Int, Int)]", RowCodecTests[Maybe[(Int, Int)]].codec[String, Float])
}