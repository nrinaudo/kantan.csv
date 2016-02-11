package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import tabulate.RowCodec
import tabulate.laws.{IllegalRow, RowCodecLaws}

trait RowCodecTests[A] extends SafeRowCodecTests[A] with RowDecoderTests[A] {
  def laws: RowCodecLaws[A]

  implicit def arbIllegalRow: Arbitrary[IllegalRow[A]]

  def rowCodec[B: Arbitrary, C: Arbitrary]: RuleSet = new RuleSet {
    def name = "rowCodec"
    def bases = Nil
    def parents = Seq(rowEncoder[B, C], rowDecoder[B, C])
    def props = Seq("round trip" → forAll(laws.roundTrip _))
  }
}

object RowCodecTests {
  def apply[A](implicit a: Arbitrary[A], c: RowCodec[A], i: Arbitrary[IllegalRow[A]]): RowCodecTests[A] = new RowCodecTests[A] {
    override def laws = RowCodecLaws[A]
    override implicit def arb = a
    override implicit def arbIllegalRow = i
  }
}
