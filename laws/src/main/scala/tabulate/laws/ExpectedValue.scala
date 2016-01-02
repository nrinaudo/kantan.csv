package tabulate.laws

import org.scalacheck.Arbitrary
import tabulate.{RowEncoder, CellEncoder}

case class ExpectedValue[A, B](value: A, encoded: B)

object ExpectedValue {
  implicit def arbitrary[A: Arbitrary](implicit ea: CellEncoder[A]): Arbitrary[ExpectedValue[A, String]] =
    Arbitrary(Arbitrary.arbitrary[A].map(a => ExpectedValue(a, ea.encode(a))))

  implicit def arbitrary[A: Arbitrary](implicit ea: RowEncoder[A]): Arbitrary[ExpectedValue[A, Seq[String]]] =
    Arbitrary(Arbitrary.arbitrary[A].map(a => ExpectedValue(a, ea.encode(a))))
}