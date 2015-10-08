package com.nrinaudo.csv.cats

import cats.laws.discipline.{ArbitraryK, MonadTests}
import com.nrinaudo.csv.DecodeResult

import com.nrinaudo.csv.cats.arbitrary._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import cats.std.int._

import _root_.cats.laws.discipline.arbitrary._


class DecodeResultTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {


  checkAll("DecodeResult[Int]", MonadTests[DecodeResult].monad[Int, Int, Int])
}