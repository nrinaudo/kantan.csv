package kantan.csv.cats

import algebra.Eq
import cats.laws.discipline.ContravariantTests
import kantan.csv.CellEncoder
import kantan.csv.laws.discipline.equality
import kantan.csv.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class CellEncoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def cellEncoderEq[A: Arbitrary]: Eq[CellEncoder[A]] = new Eq[CellEncoder[A]] {
    override def eqv(a1: CellEncoder[A], a2: CellEncoder[A]): Boolean =
      equality.cellEncoder(a1, a2)
  }

  checkAll("CellEncoder[Int]", ContravariantTests[CellEncoder].contravariant[Int, Int, Int])
}
