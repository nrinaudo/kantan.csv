package kantan.csv.cats

import _root_.cats.std.list._
import kantan.csv.laws.discipline.RowEncoderTests
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class FoldableEncoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[List[Int]] = Arbitrary(Gen.nonEmptyListOf(Arbitrary.arbitrary[Int]))
    implicit val encoder = foldableRowEncoder[List, Int]

    checkAll("Foldable[Int]", RowEncoderTests[List[Int]].encoder[List[Byte], List[Float]])
}
