package kantan.csv.laws.discipline

import java.util.UUID

import kantan.csv._
import kantan.csv.laws._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}

object arbitrary extends kantan.codecs.laws.discipline.ArbitraryInstances {
  val csv: Gen[List[List[String]]] = arb[List[List[Cell]]].map(_.map(_.map(_.value)))

  // - Errors ----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val genDecodeError: Gen[CsvError] = Gen.const(CsvError.DecodeError)
  val genReadError: Gen[CsvError] = for {
    i ← arb[Int]
    j ← arb[Int]
  } yield CsvError.ReadError(i, j)

  implicit val arbCsvError: Arbitrary[CsvError] = Arbitrary(Gen.oneOf(genDecodeError, genReadError))



  // - Encoders and decoders -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbCellDecoder[A: Arbitrary]: Arbitrary[CellDecoder[A]] =
    Arbitrary(arb[String ⇒ CsvResult[A]].map(f ⇒ CellDecoder(f)))

  implicit def arbCellEncoder[A: Arbitrary]: Arbitrary[CellEncoder[A]] =
    Arbitrary(arb[A ⇒ String].map(f ⇒ CellEncoder(f)))

  implicit def arbRowDecoder[A: Arbitrary]: Arbitrary[RowDecoder[A]] =
    Arbitrary(arb[Seq[String] ⇒ CsvResult[A]].map(f ⇒ RowDecoder(f)))

  implicit def arbRowEncoder[A: Arbitrary]: Arbitrary[RowEncoder[A]] =
    Arbitrary(arb[A ⇒ Seq[String]].map(f ⇒ RowEncoder(f)))


  // - Expected values -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbExpectedCell[A](implicit ea: CellEncoder[A], aa: Arbitrary[A]): Arbitrary[ExpectedCell[A]] =
    Arbitrary(aa.arbitrary.map(a ⇒ ExpectedValue(a, ea.encode(a))))

  implicit def arbExpectedRow[A](implicit ea: RowEncoder[A], aa: Arbitrary[A]): Arbitrary[ExpectedRow[A]] =
    Arbitrary(aa.arbitrary.map(a ⇒ ExpectedValue(a, ea.encode(a))))

  implicit def arbExpectedRowFromCell[A](implicit arb: Arbitrary[ExpectedCell[A]]): Arbitrary[ExpectedRow[A]] =
    Arbitrary(arb.arbitrary.map(a ⇒ ExpectedValue(a.value, Seq(a.encoded))))


  // - Illegal values --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def illegal[A, B](gen: Gen[B]): Arbitrary[IllegalValue[A, B]] =
    Arbitrary(gen.map(IllegalValue.apply))

  private def arbIllegalNum[A]: Arbitrary[IllegalCell[A]] = illegal(Gen.alphaChar.map(_.toString))

  implicit val arbIllegalChar: Arbitrary[IllegalCell[Char]] = illegal(for {
    h ← Arbitrary.arbitrary[Char]
    t ← Gen.nonEmptyListOf(Arbitrary.arbitrary[Char])
  } yield (h :: t).mkString)
  implicit val arbIllegalInt: Arbitrary[IllegalCell[Int]] = arbIllegalNum[Int]
  implicit val arbIllegalFloat: Arbitrary[IllegalCell[Float]] = arbIllegalNum[Float]
  implicit val arbIllegalDouble: Arbitrary[IllegalCell[Double]] = arbIllegalNum[Double]
  implicit val arbIllegalLong: Arbitrary[IllegalCell[Long]] = arbIllegalNum[Long]
  implicit val arbIllegalByte: Arbitrary[IllegalCell[Byte]] = arbIllegalNum[Byte]
  implicit val arbIllegalShort: Arbitrary[IllegalCell[Short]] = arbIllegalNum[Short]
  implicit val arbIllegalBigInt: Arbitrary[IllegalCell[BigInt]] = arbIllegalNum[BigInt]
  implicit val arbIllegalBigDecimal: Arbitrary[IllegalCell[BigDecimal]] = arbIllegalNum[BigDecimal]
  implicit val arbIllegalUUID: Arbitrary[IllegalCell[UUID]] = arbIllegalNum[UUID]
  implicit val arbIllegalBoolean: Arbitrary[IllegalCell[Boolean]] = illegal(Arbitrary.arbitrary[Int].map(_.toString))
  implicit def arbIllegalOption[A](implicit arb: Arbitrary[IllegalCell[A]]): Arbitrary[IllegalCell[Option[A]]] =
    illegal(arb.arbitrary.map(_.value))
  implicit def arbIllegalTraversable[A, C[X] <: Traversable[X]](implicit arb: Arbitrary[IllegalCell[A]]): Arbitrary[IllegalRow[C[A]]] =
    illegal(arb.arbitrary.map(s ⇒ Seq(s.value)))
}
