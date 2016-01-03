package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import tabulate.RowEncoder
import tabulate.laws._

trait RowEncoderTests[A] extends Laws {
  def laws: RowEncoderLaws[A]
  implicit def arb: Arbitrary[A]
  implicit def arbExpectedRow: Arbitrary[ExpectedRow[A]]

  def rowEncoder[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "RowEncoder",
    parent = None,
    "row encode"             -> forAll(laws.rowEncode _),
    "row encode identity"    -> forAll(laws.rowEncodeIdentity _),
    "row encode composition" -> forAll(laws.rowEncodeComposition[B, C] _)
  )
}

object RowEncoderTests {
  def apply[A](implicit a: Arbitrary[ExpectedRow[A]], c: RowEncoder[A]): RowEncoderTests[A] = new RowEncoderTests[A] {
    override def laws = RowEncoderLaws[A]
    override implicit def arbExpectedRow = a
    override implicit def arb = Arbitrary(a.arbitrary.map(_.value))
  }
}