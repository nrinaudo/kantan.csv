package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import tabulate.RowEncoder
import tabulate.laws._

trait RowEncoderTests[A] extends Laws {
  def laws: RowEncoderLaws[A]
  implicit def arbA: Arbitrary[A]
  implicit def arbExpectedA: Arbitrary[ExpectedRow[A]]

  def rowEncoder[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "RowEncoder",
    parent = None,
    "encode"             -> forAll(laws.encode _),
    "encode identity"    -> forAll(laws.encodeIdentity _),
    "encode composition" -> forAll(laws.encodeComposition[B, C] _)
  )
}

object RowEncoderTests {
  def apply[A](implicit a: Arbitrary[ExpectedRow[A]], c: RowEncoder[A]): RowEncoderTests[A] = new RowEncoderTests[A] {
    override def laws = RowEncoderLaws[A]
    override implicit def arbExpectedA = a
    override implicit def arbA = Arbitrary(a.arbitrary.map(_.value))
  }
}