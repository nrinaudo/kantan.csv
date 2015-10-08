package com.nrinaudo.csv

import org.scalacheck.Gen


package object scalacheck {
  // CSV generators for property based testing.
  val cell: Gen[String] = Gen.nonEmptyListOf(Gen.choose(0x20.toChar, 0x7e.toChar)).map(_.mkString)
  val csv: Gen[List[List[String]]] = Gen.nonEmptyListOf(Gen.nonEmptyListOf(cell))
}
