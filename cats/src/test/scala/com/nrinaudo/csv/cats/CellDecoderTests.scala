package com.nrinaudo.csv.cats

import algebra.Eq
import cats.laws.discipline.MonadTests
import com.nrinaudo.csv.{DecodeResult, CellDecoder}
import com.nrinaudo.csv.laws.discipline.arbitrary._
import com.nrinaudo.csv.cats.arbitrary._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import cats.std.int._

class CellDecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // Nasty rip straight from cats.
  implicit def eq[A: Eq]: Eq[CellDecoder[A]] = new Eq[CellDecoder[A]] {
    def eqv(c1: CellDecoder[A], c2: CellDecoder[A]): Boolean = {
      val samples = List.fill(100)(arb[String].sample).collect {
        case Some(a) => a
        case None => sys.error("Could not generate arbitrary values to compare two functions")
      }
      samples.forall(s => Eq[DecodeResult[A]].eqv(c1.decode(s), c2.decode(s)) )
    }
  }

  checkAll("CellDecoder[Int]", MonadTests[CellDecoder].monad[Int, Int, Int])
}