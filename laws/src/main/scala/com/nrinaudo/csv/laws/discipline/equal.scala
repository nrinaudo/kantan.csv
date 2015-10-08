package com.nrinaudo.csv.laws.discipline

import com.nrinaudo.csv.{RowDecoder, DecodeResult, CellDecoder}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.{arbitrary => arb}

object equal {
  def eq[A, B: Arbitrary](a1: B => A, a2: B => A)(f: (A, A) => Boolean): Boolean = {
          val samples = List.fill(100)(arb[B].sample).collect {
            case Some(a) => a
            case None => sys.error("Could not generate arbitrary values to compare two functions")
          }
          samples.forall(b => f(a1(b), a2(b)))
        }

  def cellDecoder[A](c1: CellDecoder[A], c2: CellDecoder[A])(f: (DecodeResult[A], DecodeResult[A]) => Boolean): Boolean =
    eq(c1.decode, c2.decode)(f)

  def rowDecoder[A](c1: RowDecoder[A], c2: RowDecoder[A])(f: (DecodeResult[A], DecodeResult[A]) => Boolean): Boolean =
      eq(c1.decode, c2.decode)(f)
}
