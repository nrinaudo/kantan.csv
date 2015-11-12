package tabulate.interop.cats

import tabulate.laws.discipline.arbitrary._
import algebra.Eq
import cats.laws.discipline.MonadTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import cats.std.int._
import tabulate.{DecodeResult, CellDecoder}
import tabulate.laws.discipline.equality

class CellDecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def cellDecoderEq[A: Eq]: Eq[CellDecoder[A]] = new Eq[CellDecoder[A]] {
    def eqv(c1: CellDecoder[A], c2: CellDecoder[A]): Boolean =
      equality.cellDecoder(c1, c2)(Eq[DecodeResult[A]].eqv)
  }

  checkAll("CellDecoder[Int]", MonadTests[CellDecoder].monad[Int, Int, Int])
}