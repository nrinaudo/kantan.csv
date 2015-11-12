package tabulate.interop.cats

import tabulate.laws.discipline.arbitrary._

import algebra.Eq
import cats.laws.discipline.MonadTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import cats.std.int._
import tabulate.{DecodeResult, RowDecoder}
import tabulate.laws.discipline.equality

class RowDecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def rowDecoderEq[A: Eq]: Eq[RowDecoder[A]] = new Eq[RowDecoder[A]] {
      def eqv(c1: RowDecoder[A], c2: RowDecoder[A]): Boolean =
        equality.rowDecoder(c1, c2)(Eq[DecodeResult[A]].eqv)
    }

  checkAll("RowDecoder[Int]", MonadTests[RowDecoder].monad[Int, Int, Int])
}