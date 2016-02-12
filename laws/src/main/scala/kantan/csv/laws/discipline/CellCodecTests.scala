package kantan.csv.laws.discipline

import kantan.csv.CellCodec
import kantan.csv.laws.{IllegalCell, CellCodecLaws}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._

trait CellCodecTests[A] extends SafeCellCodecTests[A] with CellDecoderTests[A] {
  def laws: CellCodecLaws[A]

  override implicit def arbExpectedRow = arbitrary.arbExpectedRowFromCell(arbExpectedCell)
  override implicit def arbIllegalRow = arbitrary.illegal(arbIllegalCell.arbitrary.map(s ⇒ Seq(s.value)))

  def cellCodec[B: Arbitrary, C: Arbitrary]: RuleSet = new RuleSet {
    def name = "cellCodec"
    def bases = Nil
    def parents = Seq(cellEncoder[B, C], cellDecoder[B, C])
    def props = Seq("round trip" → forAll(laws.roundTrip _))
  }
}

object CellCodecTests {
  def apply[A](implicit a: Arbitrary[A], c: CellCodec[A], ic: Arbitrary[IllegalCell[A]]): CellCodecTests[A] = new CellCodecTests[A] {
    override def laws = CellCodecLaws[A]
    override implicit def arb = a
    override implicit def arbIllegalCell = ic
  }
}
