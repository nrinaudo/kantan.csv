package com.nrinaudo.csv.scalaz

import com.nrinaudo.csv.laws.discipline.arbitrary._
import com.nrinaudo.csv.laws.discipline.equality
import com.nrinaudo.csv.{DecodeResult, RowDecoder}

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.monad
import scalaz.std.anyVal._

class RowDecoderTests extends ScalazSuite {
  implicit def rowDecoderEq[A: Equal]: Equal[RowDecoder[A]] = new Equal[RowDecoder[A]] {
    override def equal(a1: RowDecoder[A], a2: RowDecoder[A]): Boolean =
      equality.rowDecoder(a1, a2)(Equal[DecodeResult[A]].equal)
  }

  checkAll("RowDecoder", monad.laws[RowDecoder])
}