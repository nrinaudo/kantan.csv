package tabulate.laws.discipline

import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}
import tabulate._
import tabulate.laws._

object arbitrary {
  val csv: Gen[List[List[String]]] = arb[List[List[Cell]]].map(_.map(_.map(_.value)))

  def success[A: Arbitrary]: Gen[DecodeResult[A]] = arb[A].map(DecodeResult.success)
  def readFailure[A]: Gen[DecodeResult[A]] = arb[(Int, Int)].map(x => DecodeResult.readFailure(x._1, x._2))
  implicit def arbDecodeResult[A: Arbitrary]: Arbitrary[DecodeResult[A]] =
    Arbitrary(oneOf(const(DecodeResult.decodeFailure[A]), success[A], readFailure[A]))

  implicit def arbCellDecoder[A: Arbitrary]: Arbitrary[CellDecoder[A]] =
    Arbitrary(arb[String => DecodeResult[A]].map(f => CellDecoder(f)))

  implicit def arbCellEncoder[A: Arbitrary]: Arbitrary[CellEncoder[A]] =
    Arbitrary(arb[A => String].map(f => CellEncoder(f)))

  implicit def arbRowDecoder[A: Arbitrary]: Arbitrary[RowDecoder[A]] =
    Arbitrary(arb[Seq[String] => DecodeResult[A]].map(f => RowDecoder(f)))

  implicit def arbRowEncoder[A: Arbitrary]: Arbitrary[RowEncoder[A]] =
    Arbitrary(arb[A => Seq[String]].map(f => RowEncoder(f)))

  implicit def arbExpectedCell[A: Arbitrary](implicit ea: CellEncoder[A]): Arbitrary[ExpectedCell[A]] =
    Arbitrary(Arbitrary.arbitrary[A].map(a => ExpectedValue(a, ea.encode(a))))

  implicit def arbExpectedRow[A: Arbitrary](implicit ea: RowEncoder[A]): Arbitrary[ExpectedRow[A]] =
    Arbitrary(Arbitrary.arbitrary[A].map(a => ExpectedValue(a, ea.encode(a))))
}
