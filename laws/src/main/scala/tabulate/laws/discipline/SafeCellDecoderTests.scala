package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import tabulate.CellDecoder
import tabulate.laws._

trait SafeCellDecoderTests[A] extends Laws {
  def laws: SafeCellDecoderLaws[A]
  implicit def arbExpectedA: Arbitrary[ExpectedCell[A]]

  def safeCellDecoder[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "safeCellDecoder",
    parent = None,
    "decode"               -> forAll(laws.decode _),
    "safe out-of-bounds"   -> forAll(laws.safeOutOfBounds _),
    "unsafe out-of-bounds" -> forAll(laws.unsafeOutputOfBounds _),
    "decode identity"      -> forAll(laws.decodeIdentity _),
    "decode composition"   -> forAll(laws.decodeComposition[B, C] _)
  )
}

object SafeCellDecoderTests {
  def apply[A: CellDecoder](implicit a: Arbitrary[ExpectedCell[A]]): SafeCellDecoderTests[A] = new SafeCellDecoderTests[A] {
    override def laws = SafeCellDecoderLaws[A]
    override implicit def arbExpectedA = a
  }
}