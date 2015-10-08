package com.nrinaudo.csv.laws.discipline

import com.nrinaudo.csv.CellCodec
import com.nrinaudo.csv.laws.CellCodecLaws
import org.scalacheck.Arbitrary
import org.typelevel.discipline.Laws
import org.scalacheck.Prop
import Prop._

trait CellCodecTests[A] extends Laws {
  def laws: CellCodecLaws[A]
  implicit def arbA: Arbitrary[A]

  def cellCodec[B: Arbitrary, C: Arbitrary]: RuleSet = new DefaultRuleSet(
    name = "cellCodec",
    parent = None,
    "encode reversibility " -> forAll(laws.encodeReversibility _),
    "decode identity" -> forAll(laws.decodeIdentity _),
    "encode identity" -> forAll(laws.encodeIdentity _),
    "decode composition" -> forAll(laws.decodeComposition[B, C] _),
    "encode composition" -> forAll(laws.decodeComposition[B, C] _)
  )
}

object CellCodecTests {
  def apply[A](implicit a: Arbitrary[A], c: CellCodec[A]): CellCodecTests[A] = new CellCodecTests[A] {
    override def laws = CellCodecLaws[A]
    override implicit def arbA = a
  }
}
