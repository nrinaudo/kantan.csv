package kantan.csv.cats

import algebra.Eq
import cats.laws.discipline.MonadTests
import cats.std.int._
import kantan.csv.cats.eqs._
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.equality
import kantan.csv.{CellDecoder, DecodeResult}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class CellDecoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def cellDecoderEq[A: Eq]: Eq[CellDecoder[A]] = new Eq[CellDecoder[A]] {
    def eqv(c1: CellDecoder[A], c2: CellDecoder[A]): Boolean =
      equality.cellDecoder(c1, c2)(Eq[DecodeResult[A]].eqv)
  }

  checkAll("CellDecoder[Int]", MonadTests[CellDecoder].monad[Int, Int, Int])
}