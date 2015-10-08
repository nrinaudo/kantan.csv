package com.nrinaudo.csv

import com.nrinaudo.csv.laws.discipline.RowCodecTests
import org.scalacheck.Arbitrary._
import org.scalacheck.{Gen, Arbitrary}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class SeqTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[Seq[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Seq, Int](arbitrary[Int]))

  checkAll("Seq[Int]", RowCodecTests[Seq[Int]].rowCodec[List[String], List[Float]])
}
