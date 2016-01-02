package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import tabulate.laws._

trait CellDecoderTests[A] extends SafeCellDecoderTests[A] {
  def laws: CellDecoderLaws[A]
  implicit def arbExpectedA: Arbitrary[ExpectedCell[A]]
  implicit def arbIllegalA: Arbitrary[IllegalValue[A]]

  def cellDecoder[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "cellDecoder",
    parent = Some(safeCellDecoder[B, C]),
    "safe decode fail"     -> forAll(laws.safeDecodeFail _),
    "unsafe decode fail"   -> forAll(laws.unsafeDecodeFail _)
  )
}
