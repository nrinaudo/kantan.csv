package tabulate.interop.cats

import algebra.Eq
import cats.laws.discipline.ContravariantTests
import org.scalacheck.Arbitrary
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.CellEncoder
import tabulate.laws.discipline.arbitrary._
import tabulate.laws.discipline.equality


class CellEncoderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit def cellEncoderEq[A: Arbitrary]: Eq[CellEncoder[A]] = new Eq[CellEncoder[A]] {
    override def eqv(a1: CellEncoder[A], a2: CellEncoder[A]): Boolean =
      equality.cellEncoder(a1, a2)
  }

  checkAll("CellEncoder[Int]", ContravariantTests[CellEncoder].contravariant[Int, Int, Int])
}
