package com.nrinaudo.csv.cats

import algebra.Eq
import cats.laws.discipline.MonadTests
import com.nrinaudo.csv.{DecodeResult, CellDecoder}
import com.nrinaudo.csv.laws.discipline.arbitrary._
import com.nrinaudo.csv.laws.discipline._
import com.nrinaudo.csv.cats.arbitrary._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import cats.std.int._

class CellDecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def cellDecoderEq[A: Eq]: Eq[CellDecoder[A]] = new Eq[CellDecoder[A]] {
    def eqv(c1: CellDecoder[A], c2: CellDecoder[A]): Boolean =
      equal.cellDecoder(c1, c2)(Eq[DecodeResult[A]].eqv)
  }

  checkAll("CellDecoder[Int]", MonadTests[CellDecoder].monad[Int, Int, Int])
}