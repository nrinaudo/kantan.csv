package kantan.csv

import kantan.csv.laws.discipline.RowCodecTests
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ListCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[List[Int]] = Arbitrary(Gen.nonEmptyListOf(Arbitrary.arbitrary[Int]))

  checkAll("RowCodec[List[Int]]", RowCodecTests[List[Int]].codec[List[String], List[Float]])
}
