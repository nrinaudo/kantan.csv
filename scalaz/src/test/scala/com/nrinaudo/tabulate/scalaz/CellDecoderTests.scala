package com.nrinaudo.tabulate.scalaz

import com.nrinaudo.tabulate.laws.discipline.equality
import com.nrinaudo.tabulate.laws.discipline.arbitrary._
import com.nrinaudo.tabulate.{CellDecoder, DecodeResult}

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.monad
import scalaz.std.anyVal._

class CellDecoderTests extends ScalazSuite {
  implicit def cellDecoderEq[A: Equal]: Equal[CellDecoder[A]] = new Equal[CellDecoder[A]] {
    override def equal(a1: CellDecoder[A], a2: CellDecoder[A]): Boolean =
      equality.cellDecoder(a1, a2)(Equal[DecodeResult[A]].equal)
  }

  checkAll("CellDecoder", monad.laws[CellDecoder])
}