package tabulate.laws.discipline

import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}
import tabulate._
import tabulate.laws.Cell

object arbitrary {
  val nonEscapedChar: Gen[Char] = Gen.choose(0x20.toChar, 0x7e.toChar)
  val escapedChar: Gen[Char] = Gen.choose(0x20.toChar, 0x7e.toChar)

  val emptyCell: Gen[Cell] = Gen.const(Cell.Empty)
  val escapedCell: Gen[Cell] = Gen.nonEmptyListOf(escapedChar).map(v => Cell(v.mkString))
  val nonEscapedCell: Gen[Cell] = Gen.nonEmptyListOf(nonEscapedChar).map(v => Cell(v.mkString))



  // CSV generators for property based testing.
  val cell: Gen[String] = Gen.nonEmptyListOf(Gen.choose(0x20.toChar, 0x7e.toChar)).map(_.mkString)
  val csv: Gen[List[List[String]]] = Gen.nonEmptyListOf(Gen.nonEmptyListOf(cell))

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
}
