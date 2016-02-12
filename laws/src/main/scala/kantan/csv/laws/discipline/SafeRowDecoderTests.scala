package kantan.csv.laws.discipline

import kantan.csv.RowDecoder
import kantan.csv.laws.{ExpectedRow, SafeRowDecoderLaws}
import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

trait SafeRowDecoderTests[A] extends Laws {
  def laws: SafeRowDecoderLaws[A]
  implicit def arbExpectedRow: Arbitrary[ExpectedRow[A]]

  def safeRowDecoder[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "safeRowDecoder",
    parent = None,
    "row decode"             → forAll(laws.rowDecode _),
    "row decode identity"    → forAll(laws.rowDecodeIdentity _),
    "row decode composition" → forAll(laws.rowDecodeComposition[B, C] _)
  )
}

object SafeRowDecoderTests {
  def apply[A: RowDecoder](implicit a: Arbitrary[ExpectedRow[A]]): SafeRowDecoderTests[A] = new SafeRowDecoderTests[A] {
    override def laws = SafeRowDecoderLaws[A]
    override implicit def arbExpectedRow = a
  }
}