package com.nrinaudo.tabulate.scalaz

import com.nrinaudo.tabulate.DecodeResult
import com.nrinaudo.tabulate.laws.discipline.arbitrary._

import scalaz.scalacheck.ScalazProperties.{equal, monad}
import scalaz.std.anyVal._

class DecodeResultTests extends ScalazSuite {
  checkAll("DecodeResult", monad.laws[DecodeResult])
  checkAll("DecodeResult[Int]", equal.laws[DecodeResult[Int]])
}
