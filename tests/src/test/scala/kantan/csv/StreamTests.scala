package kantan.csv

import kantan.csv.laws.discipline.RowCodecTests
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class StreamTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[Stream[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Stream, Int](Arbitrary.arbitrary[Int]))

  checkAll("Stream[Int]", RowCodecTests[Stream[Int]].codec[List[String], List[Float]])
}
