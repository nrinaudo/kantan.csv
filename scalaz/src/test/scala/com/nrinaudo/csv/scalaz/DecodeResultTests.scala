package com.nrinaudo.csv.scalaz

import com.nrinaudo.csv.DecodeResult

import scalaz.scalacheck.ScalazProperties.monad
import com.nrinaudo.csv.laws.discipline.arbitrary._
import scalaz.std.anyVal._

class DecodeResultTests extends ScalazSuite {
  checkAll("DecodeResult", monad.laws[DecodeResult])
}
