package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import tabulate.CellEncoder
import tabulate.laws._

trait CellEncoderTests[A] extends RowEncoderTests[A] {
  def laws: CellEncoderLaws[A]
  implicit def arbA: Arbitrary[A]
  implicit def arbExpectedCellA: Arbitrary[ExpectedCell[A]]
  implicit def arbExpectedRowA: Arbitrary[ExpectedRow[A]]

  def cellEncoder[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "cellEncoder",
    parent = Some(rowEncoder[B, C]),
    "cell encode"             -> forAll(laws.cellEncode _),
    "cell encode identity"    -> forAll(laws.cellEncodeIdentity _),
    "cell encode composition" -> forAll(laws.cellEncodeComposition[B, C] _)
  )
}

object CellEncoderTests {
  def apply[A](implicit ac: Arbitrary[ExpectedCell[A]], ar: Arbitrary[ExpectedRow[A]], c: CellEncoder[A]): CellEncoderTests[A] = new CellEncoderTests[A] {
    override def laws = CellEncoderLaws[A]
    override implicit def arbExpectedCellA = ac
    override implicit def arbExpectedRowA = ar
    override implicit def arbA = Arbitrary(ac.arbitrary.map(_.value))
  }
}