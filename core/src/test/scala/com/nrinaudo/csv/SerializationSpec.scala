package com.nrinaudo.csv

import com.nrinaudo.csv.scalacheck._
import com.nrinaudo.csv.tools._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class SerializationSpec extends FunSuite with GeneratorDrivenPropertyChecks {
  test("Serialized CSV data should be parsed correctly") {
    forAll(csv) { ss: List[List[String]] =>
      assert(read(write(ss)) == ss)
    }
  }
}
