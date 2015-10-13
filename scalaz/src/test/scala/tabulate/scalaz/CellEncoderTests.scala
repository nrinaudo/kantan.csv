package tabulate.scalaz

import tabulate.CellEncoder
import tabulate.laws.discipline.arbitrary
import arbitrary._
import org.scalacheck.Arbitrary
import tabulate.laws.discipline.equality

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.contravariant

class CellEncoderTests extends ScalazSuite {
  implicit def cellEncoderEq[A: Arbitrary]: Equal[CellEncoder[A]] = new Equal[CellEncoder[A]] {
      override def equal(a1: CellEncoder[A], a2: CellEncoder[A]): Boolean =
        equality.cellEncoder(a1, a2)
    }

  checkAll("CellEncoder", contravariant.laws[CellEncoder])
}