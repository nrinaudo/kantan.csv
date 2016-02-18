package kantan.csv.cats

import cats.Eq
import cats.laws.discipline.ContravariantTests
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import kantan.csv.RowEncoder
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.equality

class RowEncoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def rowEncoderEq[A: Arbitrary]: Eq[RowEncoder[A]] = new Eq[RowEncoder[A]] {
    override def eqv(a1: RowEncoder[A], a2: RowEncoder[A]): Boolean =
      equality.rowEncoder(a1, a2)
  }

  checkAll("RowEncoder[Int]", ContravariantTests[RowEncoder].contravariant[Int, Int, Int])
}
