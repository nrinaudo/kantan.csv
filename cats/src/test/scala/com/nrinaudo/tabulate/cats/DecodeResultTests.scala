package com.nrinaudo.tabulate.cats

import arbitrary._
import cats.laws.discipline.MonadTests

import com.nrinaudo.tabulate.DecodeResult
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import cats.std.int._


class DecodeResultTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("DecodeResult[Int]", MonadTests[DecodeResult].monad[Int, Int, Int])
}