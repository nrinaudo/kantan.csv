package kantan.csv.scalaz

import kantan.csv.CellEncoder
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.equality
import org.scalacheck.Arbitrary

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.contravariant

class CellEncoderTests extends ScalazSuite {
  implicit def cellEncoderEq[A: Arbitrary]: Equal[CellEncoder[A]] = new Equal[CellEncoder[A]] {
      override def equal(a1: CellEncoder[A], a2: CellEncoder[A]): Boolean =
        equality.cellEncoder(a1, a2)
    }

  checkAll("CellEncoder", contravariant.laws[CellEncoder])
}