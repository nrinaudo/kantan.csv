package com.nrinaudo.tabulate

import com.nrinaudo.tabulate.laws.discipline.CellCodecTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class FloatTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Float", CellCodecTests[Float].cellCodec[String, Float])
}