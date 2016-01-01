package tabulate.laws

import org.scalacheck.Arbitrary
import tabulate.CellEncoder

case class ExpectedValue[A](value: A, encoded: String)

object ExpectedValue {
  implicit def arbitrary[A: Arbitrary](implicit ea: CellEncoder[A]): Arbitrary[ExpectedValue[A]] =
    Arbitrary(Arbitrary.arbitrary[A].map(a => ExpectedValue(a, ea.encode(a))))
}