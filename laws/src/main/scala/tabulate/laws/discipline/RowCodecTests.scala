package tabulate.laws.discipline

import org.scalacheck.{Gen, Arbitrary}
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
    "encode round trip" -> forAll(laws.roundTrip _),
    "decode identity" -> forAll(laws.decodeIdentity _),
    "encode identity" -> forAll(laws.encodeIdentity _),
    "decode composition" -> forAll(laws.decodeComposition[B, C] _),
    "encode composition" -> forAll(laws.encodeComposition[B, C] _)
  )

  def reversibleRowCodec[B: Arbitrary, C: Arbitrary]: RuleSet = {
    // Overrides the default Arbitrary[List[String]] to generate non-empty lists of non-empty strings.
    implicit val header = Arbitrary(Gen.nonEmptyListOf(Gen.nonEmptyListOf(Gen.alphaNumChar).map(_.mkString)))
    new DefaultRuleSet(
      name = "reversibleRowCodec",
      parent = Some(rowCodec[B, C]),
      "csv round trip" -> forAll(laws.csvRoundTrip _))
  }
}

object RowCodecTests {
  def apply[A](implicit a: Arbitrary[A], c: RowCodec[A]): RowCodecTests[A] = new RowCodecTests[A] {
    override def laws = RowCodecLaws[A]
    override implicit def arbA = a
  }
}
