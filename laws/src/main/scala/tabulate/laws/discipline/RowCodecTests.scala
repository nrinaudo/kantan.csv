package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import tabulate.RowCodec
import tabulate.laws.RowCodecLaws

trait RowCodecTests[A] extends Laws {
  def laws: RowCodecLaws[A]
  implicit def arbA: Arbitrary[A]

  def rowCodec[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "rowCodec",
    parent = None,
    "encode reversibility " -> forAll(laws.encodeReversibility _),
    "decode identity" -> forAll(laws.decodeIdentity _),
    "encode identity" -> forAll(laws.encodeIdentity _),
    "decode composition" -> forAll(laws.decodeComposition[B, C] _),
    "encode composition" -> forAll(laws.encodeComposition[B, C] _)
  )

  def reversibleRowCodec[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "reversibleRowCodec",
    parent = Some(rowCodec[B, C]),
    "csv reversibility" -> forAll(laws.csvReversibility _))
}

object RowCodecTests {
  def apply[A](implicit a: Arbitrary[A], c: RowCodec[A]): RowCodecTests[A] = new RowCodecTests[A] {
    override def laws = RowCodecLaws[A]
    override implicit def arbA = a
  }
}
