package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import tabulate.CellCodec
import tabulate.laws._

trait SafeCellCodecTests[A] extends CellEncoderTests[A] with SafeCellDecoderTests[A] {
  def laws: SafeCellCodecLaws[A]

  implicit def arbA: Arbitrary[A]
  implicit val arbExpectedA: Arbitrary[ExpectedCell[A]] = arbitrary.arbExpectedCell(laws.encoder, arbA)

  def safeCellCodec[B: Arbitrary, C: Arbitrary]: RuleSet = new RuleSet {
    def name = "safeCellCodec"
    def bases = Nil
    def parents = Seq(cellEncoder[B, C], safeCellDecoder[B, C])
    def props = Seq("round trip" -> forAll(laws.roundTrip _))
  }
}

object SafeCellCodecTests {
  def apply[A](implicit a: Arbitrary[A], c: CellCodec[A]): SafeCellCodecTests[A] = new SafeCellCodecTests[A] {
    override def laws = SafeCellCodecLaws[A]
    override implicit def arbA = a
  }
}
