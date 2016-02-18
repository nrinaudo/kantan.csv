package kantan.csv.cats

import cats.Eq
import cats.laws.discipline.FunctorTests
import cats.std.int._
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.equality
import kantan.csv.{CsvResult, RowDecoder}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class RowDecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicitly[Eq[CsvResult[Int]]]

  implicit def rowDecoderEq[A: Eq]: Eq[RowDecoder[A]] = new Eq[RowDecoder[A]] {
    def eqv(c1: RowDecoder[A], c2: RowDecoder[A]): Boolean =
      equality.rowDecoder(c1, c2)(Eq[CsvResult[A]].eqv)
  }

  checkAll("RowDecoder[Int]", FunctorTests[RowDecoder].functor[Int, Int, Int])
}