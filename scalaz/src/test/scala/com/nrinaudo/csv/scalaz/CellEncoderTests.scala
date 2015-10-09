package com.nrinaudo.csv.scalaz

import com.nrinaudo.csv.laws.discipline.equality
import com.nrinaudo.csv.{DecodeResult, CellDecoder, CellEncoder}
import com.nrinaudo.csv.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.contravariant

class CellEncoderTests extends ScalazSuite {
  implicit def cellEncoderEq[A: Arbitrary]: Equal[CellEncoder[A]] = new Equal[CellEncoder[A]] {
      override def equal(a1: CellEncoder[A], a2: CellEncoder[A]): Boolean =
        equality.cellEncoder(a1, a2)
    }

  checkAll("CellEncoder", contravariant.laws[CellEncoder])
}