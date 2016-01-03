package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import tabulate.RowDecoder
import tabulate.laws._

trait RowDecoderTests[A] extends SafeRowDecoderTests[A] {
  def laws: RowDecoderLaws[A]
  implicit def arbIllegalRow: Arbitrary[IllegalRow[A]]

  def rowDecoder[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "rowDecoder",
    parent = Some(safeRowDecoder[B, C]),
    "safe row decode fail"     -> forAll(laws.safeRowDecodeFail _),
    "unsafe row decode fail"   -> forAll(laws.unsafeRowDecodeFail _)
  )
}

object RowDecoderTests {
  def apply[A](implicit a: Arbitrary[ExpectedRow[A]], c: RowDecoder[A], i: Arbitrary[IllegalRow[A]]): RowDecoderTests[A] = new RowDecoderTests[A] {
    override def laws = RowDecoderLaws[A]
    override implicit def arbExpectedRow = a
    override implicit def arbIllegalRow = i
  }
}