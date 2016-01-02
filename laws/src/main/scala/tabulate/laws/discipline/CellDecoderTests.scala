package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import tabulate.CellDecoder
import tabulate.laws._

trait CellDecoderTests[A] extends SafeCellDecoderTests[A] {
  def laws: CellDecoderLaws[A]
  implicit def arbIllegalA: Arbitrary[IllegalCell[A]]

  def cellDecoder[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "cellDecoder",
    parent = Some(safeCellDecoder[B, C]),
    "safe decode fail"     -> forAll(laws.safeDecodeFail _),
    "unsafe decode fail"   -> forAll(laws.unsafeDecodeFail _)
  )
}

object CellDecoderTests {
  def apply[A](implicit a: Arbitrary[ExpectedCell[A]], c: CellDecoder[A], i: Arbitrary[IllegalCell[A]]): CellDecoderTests[A] = new CellDecoderTests[A] {
    override def laws = CellDecoderLaws[A]
    override implicit def arbExpectedA = a
    override implicit def arbIllegalA = i
  }
}