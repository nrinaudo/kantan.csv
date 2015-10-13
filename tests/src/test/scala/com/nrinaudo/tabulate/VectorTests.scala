package com.nrinaudo.tabulate

import com.nrinaudo.tabulate.laws.discipline.RowCodecTests
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class VectorTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[Vector[Int]] = Arbitrary(Gen.nonEmptyContainerOf[Vector, Int](arbitrary[Int]))

  checkAll("Vector[Int]", RowCodecTests[Vector[Int]].rowCodec[List[String], List[Float]])
}
