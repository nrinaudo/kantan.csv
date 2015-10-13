package com.nrinaudo.tabulate.cats

import arbitrary._
import algebra.Eq
import cats.laws.discipline.MonadTests
import com.nrinaudo.tabulate.laws.discipline.equality
import com.nrinaudo.tabulate.{CellDecoder, DecodeResult}
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import cats.std.int._

class CellDecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def cellDecoderEq[A: Eq]: Eq[CellDecoder[A]] = new Eq[CellDecoder[A]] {
    def eqv(c1: CellDecoder[A], c2: CellDecoder[A]): Boolean =
      equality.cellDecoder(c1, c2)(Eq[DecodeResult[A]].eqv)
  }

  checkAll("CellDecoder[Int]", MonadTests[CellDecoder].monad[Int, Int, Int])
}