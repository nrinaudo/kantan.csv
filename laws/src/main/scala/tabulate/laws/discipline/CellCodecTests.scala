package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws
import org.scalacheck.Prop
import Prop._
import tabulate.CellCodec
import tabulate.laws.CellCodecLaws

trait CellCodecTests[A] extends Laws {
  def laws: CellCodecLaws[A]
  implicit def arbA: Arbitrary[A]

  def cellCodec[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "cellCodec",
    parent = None,
    "round trip"         -> forAll(laws.roundTrip _),
    "decode identity"    -> forAll(laws.decodeIdentity _),
    "encode identity"    -> forAll(laws.encodeIdentity _),
    "decode composition" -> forAll(laws.decodeComposition[B, C] _),
    "encode composition" -> forAll(laws.encodeComposition[B, C] _)
  )
}

object CellCodecTests {
  def apply[A](implicit a: Arbitrary[A], c: CellCodec[A]): CellCodecTests[A] = new CellCodecTests[A] {
    override def laws = CellCodecLaws[A]
    override implicit def arbA = a
  }
}
