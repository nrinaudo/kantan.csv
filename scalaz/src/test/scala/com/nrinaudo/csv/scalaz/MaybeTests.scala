package com.nrinaudo.csv.scalaz

import com.nrinaudo.csv.laws.discipline.CellCodecTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

import _root_.scalaz.scalacheck.ScalazArbitrary._
import scalaz.Maybe

class MaybeTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Maybe[Int]", CellCodecTests[Maybe[Int]].cellCodec[String, Float])
}