package com.nrinaudo.csv

import com.nrinaudo.csv.laws.discipline.CellCodecTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigIntTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("BigInt", CellCodecTests[BigInt].cellCodec[String, Float])
}