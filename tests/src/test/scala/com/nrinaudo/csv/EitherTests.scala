package com.nrinaudo.csv

import com.nrinaudo.csv.laws.discipline.{RowCodecTests, CellCodecTests}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class EitherTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("Either[Int, Boolean]", CellCodecTests[Either[Int, Boolean]].cellCodec[Byte, Float])
  checkAll("Either[(Int, Int, Int), (Boolean, String)]", RowCodecTests[Either[(Int, Int, Int), (Boolean, String)]].rowCodec[Byte, Float])
}