package com.nrinaudo.tabulate.scalaz

import com.nrinaudo.tabulate.laws.discipline.equality
import com.nrinaudo.tabulate.laws.discipline.arbitrary._
import com.nrinaudo.tabulate.{RowDecoder, DecodeResult}

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