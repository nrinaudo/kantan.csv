package com.nrinaudo.csv.scalaz

import com.nrinaudo.csv.laws.discipline.{RowCodecTests, CellCodecTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline


import scalaz.Maybe
import _root_.scalaz.scalacheck.ScalazArbitrary._

class MaybeTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Maybe[Int]", CellCodecTests[Maybe[Int]].cellCodec[String, Float])
}