package kantan.csv.scalaz

import kantan.csv.laws.discipline.RowEncoderTests
import org.scalacheck.{Arbitrary, Gen}
import scalaz.std.list._

class FoldableEncoderTests extends ScalazSuite {
  implicit val arb: Arbitrary[List[Int]] = Arbitrary(Gen.nonEmptyListOf(Arbitrary.arbitrary[Int]))
  implicit val encoder = foldableRowEncoder[List, Int]

  checkAll("Foldable[Int]", RowEncoderTests[List[Int]].encoder[List[Byte], List[Float]])
}
