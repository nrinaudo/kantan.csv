package com.nrinaudo.csv.cats

import algebra.Eq
import cats.laws.discipline.MonadTests
import com.nrinaudo.csv.{RowDecoder, DecodeResult}
import com.nrinaudo.csv.laws.discipline.arbitrary._
import com.nrinaudo.csv.cats.arbitrary._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import cats.std.int._

class RowDecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // Nasty rip straight from cats.
  implicit def eq[A: Eq]: Eq[RowDecoder[A]] = new Eq[RowDecoder[A]] {
    def eqv(c1: RowDecoder[A], c2: RowDecoder[A]): Boolean = {
      val samples = List.fill(100)(arb[Seq[String]].sample).collect {
        case Some(a) => a
        case None => sys.error("Could not generate arbitrary values to compare two functions")
      }
      samples.forall(s => Eq[DecodeResult[A]].eqv(c1.decode(s), c2.decode(s)) )
    }
  }

  checkAll("RowDecoder[Int]", MonadTests[RowDecoder].monad[Int, Int, Int])
}