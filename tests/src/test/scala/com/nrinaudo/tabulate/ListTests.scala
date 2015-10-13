package com.nrinaudo.tabulate

import com.nrinaudo.tabulate.laws.discipline.RowCodecTests
import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ListTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arb: Arbitrary[List[Int]] = Arbitrary(Gen.nonEmptyListOf(arbitrary[Int]))

  checkAll("List[Int]", RowCodecTests[List[Int]].rowCodec[List[String], List[Float]])
}
