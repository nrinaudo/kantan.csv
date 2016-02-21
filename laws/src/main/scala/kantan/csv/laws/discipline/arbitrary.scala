package kantan.csv.laws.discipline

import kantan.csv._
import kantan.csv.laws._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.{Arbitrary, Gen}

object arbitrary extends ArbitraryArities {
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


  // - Codec values ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbLegalOptionRow[D](implicit dl: Arbitrary[LegalRow[D]]): Arbitrary[LegalRow[Option[D]]] =
    Arbitrary(genLegalOption[Seq[String], D](_.isEmpty)(Seq.empty))

  implicit def arbIllegalOptionRow[D](implicit dl: Arbitrary[IllegalRow[D]]): Arbitrary[IllegalRow[Option[D]]] =
    Arbitrary(genIllegalOption[Seq[String], D](_.isEmpty))
}
