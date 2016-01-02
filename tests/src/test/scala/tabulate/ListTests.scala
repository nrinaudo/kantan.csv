package tabulate

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.IllegalValue
import tabulate.laws.discipline.RowCodecTests
import tabulate.laws.discipline.arbitrary._

class ListTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[List[Int]] = Arbitrary(Gen.nonEmptyListOf(arbitrary[Int]))

  checkAll("List[Int]", RowCodecTests[List[Int]].rowCodec[List[String], List[Float]])
}
