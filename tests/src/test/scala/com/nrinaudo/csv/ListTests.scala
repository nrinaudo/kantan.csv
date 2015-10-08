package com.nrinaudo.csv

import com.nrinaudo.csv.laws.discipline.RowCodecTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ListTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("List[Int]", RowCodecTests[List[Int]].rowCodec[List[String], List[Float]])
}
