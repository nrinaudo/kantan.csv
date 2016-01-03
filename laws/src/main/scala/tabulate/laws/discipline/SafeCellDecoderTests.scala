package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import tabulate.CellDecoder
import tabulate.laws._

trait SafeCellDecoderTests[A] extends SafeRowDecoderTests[A] {
  def laws: SafeCellDecoderLaws[A]
  implicit def arbExpectedCellA: Arbitrary[ExpectedCell[A]]
  implicit def arbExpectedRowA: Arbitrary[ExpectedRow[A]]

  def safeCellDecoder[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "safeCellDecoder",
    parent = Some(safeRowDecoder[B, C]),
    "cell decode"               -> forAll(laws.cellDecode _),
    "safe out-of-bounds"   -> forAll(laws.safeOutOfBounds _),
    "unsafe out-of-bounds" -> forAll(laws.unsafeOutputOfBounds _),
    "cell decode identity"      -> forAll(laws.cellDecodeIdentity _),
    "cell decode composition"   -> forAll(laws.cellDecodeComposition[B, C] _)
  )
}

object SafeCellDecoderTests {
  def apply[A: CellDecoder](implicit ac: Arbitrary[ExpectedCell[A]], ar: Arbitrary[ExpectedRow[A]]): SafeCellDecoderTests[A] = new SafeCellDecoderTests[A] {
    override def laws = SafeCellDecoderLaws[A]
    override implicit def arbExpectedCellA = ac
    override implicit def arbExpectedRowA = ar
  }
}