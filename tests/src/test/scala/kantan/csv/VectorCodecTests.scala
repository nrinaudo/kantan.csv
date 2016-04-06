package kantan.csv

import kantan.csv.laws.discipline.RowCodecTests
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class VectorCodecTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[Vector[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Vector, Int](Arbitrary.arbitrary[Int]))

  checkAll("RowCodec[Vector[Int]]", RowCodecTests[Vector[Int]].codec[List[String], List[Float]])
}
