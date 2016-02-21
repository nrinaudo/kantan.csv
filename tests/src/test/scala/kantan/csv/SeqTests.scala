package kantan.csv

import kantan.csv.laws.discipline.RowCodecTests
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class SeqTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[Seq[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Seq, Int](Arbitrary.arbitrary[Int]))

  checkAll("Seq[Int]", RowCodecTests[Seq[Int]].codec[List[String], List[Float]])
}
