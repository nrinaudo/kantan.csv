package com.nrinaudo.csv.scalaz

import com.nrinaudo.csv.DecodeResult

import scalaz.scalacheck.ScalazProperties.{equal, monad}
import com.nrinaudo.csv.laws.discipline.arbitrary._
import scalaz.std.anyVal._

class DecodeResultTests extends ScalazSuite {
  checkAll("DecodeResult", monad.laws[DecodeResult])
  checkAll("DecodeResult[Int]", equal.laws[DecodeResult[Int]])
}
