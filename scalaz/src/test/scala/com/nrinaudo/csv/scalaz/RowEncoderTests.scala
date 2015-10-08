package com.nrinaudo.csv.scalaz

import com.nrinaudo.csv.RowEncoder
import com.nrinaudo.csv.laws.discipline.arbitrary._
import com.nrinaudo.csv.laws.discipline.equality
import org.scalacheck.Arbitrary

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.contravariant

class RowEncoderTests extends ScalazSuite {
  implicit def rowEncoderEq[A: Arbitrary]: Equal[RowEncoder[A]] = new Equal[RowEncoder[A]] {
      override def equal(a1: RowEncoder[A], a2: RowEncoder[A]): Boolean =
        equality.rowEncoder(a1, a2)
    }

  checkAll("RowEncoder", contravariant.laws[RowEncoder])
}