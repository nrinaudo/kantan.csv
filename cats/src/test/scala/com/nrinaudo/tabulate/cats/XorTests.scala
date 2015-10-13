package com.nrinaudo.tabulate.cats

import com.nrinaudo.tabulate.laws.discipline.{RowCodecTests, CellCodecTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

import _root_.cats.data.Xor
import _root_.cats.laws.discipline.arbitrary._


class XorTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Xor[Int, Boolean]", CellCodecTests[Xor[Int, Boolean]].cellCodec[Byte, Float])
  checkAll("Xor[(Int, Int, Int), (Boolean, Float)]", RowCodecTests[Xor[(Int, Int, Int), (Boolean, Float)]].rowCodec[Byte, String])
}