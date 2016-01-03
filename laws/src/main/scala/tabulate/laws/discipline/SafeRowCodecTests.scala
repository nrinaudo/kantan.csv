package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import tabulate.RowCodec
import tabulate.laws._

trait SafeRowCodecTests[A] extends RowEncoderTests[A] with SafeRowDecoderTests[A] {
  def laws: SafeRowCodecLaws[A]

  implicit def arbA: Arbitrary[A]
  override implicit val arbExpectedRowA: Arbitrary[ExpectedRow[A]] = arbitrary.arbExpectedRow(laws.rowEncoder, arbA)

  def safeRowCodec[B: Arbitrary, C: Arbitrary]: RuleSet = new RuleSet {
    def name = "safeRowCodec"
    def bases = Nil
    def parents = Seq(rowEncoder[B, C], safeRowDecoder[B, C])
    def props = Seq("round trip" -> forAll(laws.roundTrip _))
  }
}

object SafeRowCodecTests {
  def apply[A](implicit a: Arbitrary[A], c: RowCodec[A]): SafeRowCodecTests[A] = new SafeRowCodecTests[A] {
    override def laws = SafeRowCodecLaws[A]
    override implicit def arbA = a
  }
}
