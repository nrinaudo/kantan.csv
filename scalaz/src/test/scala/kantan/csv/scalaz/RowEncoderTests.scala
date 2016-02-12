package kantan.csv.scalaz

import kantan.csv.RowEncoder
import kantan.csv.laws.discipline.equality
import kantan.csv.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.contravariant

class RowEncoderTests extends ScalazSuite {
  implicit def rowEncoderEq[A: Arbitrary]: Equal[RowEncoder[A]] = new Equal[RowEncoder[A]] {
      override def equal(a1: RowEncoder[A], a2: RowEncoder[A]): Boolean =
        equality.rowEncoder(a1, a2)
    }

  checkAll("RowEncoder", contravariant.laws[RowEncoder])
}