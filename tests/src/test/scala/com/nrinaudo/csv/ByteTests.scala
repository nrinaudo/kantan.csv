package com.nrinaudo.csv

import com.nrinaudo.csv.laws.discipline.CellCodecTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ByteTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Byte", CellCodecTests[Byte].cellCodec[String, Float])
}