package com.nrinaudo.csv.scalaz

import com.nrinaudo.csv.laws.discipline.{RowCodecTests, CellCodecTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline


import _root_.scalaz.\/
import _root_.scalaz.scalacheck.ScalazArbitrary._

class EitherTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Int \\/ Boolean", CellCodecTests[Int \/ Boolean].cellCodec[String, Float])
  checkAll("(Int, Int, Int) \\/ (Boolean, String)", RowCodecTests[(Int, Int, Int) \/ (Boolean, String)].rowCodec[String, Float])
}