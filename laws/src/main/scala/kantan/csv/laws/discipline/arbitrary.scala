package kantan.csv.laws.discipline

import java.io.IOException
import kantan.csv._
import kantan.csv.laws._
import org.scalacheck.Arbitrary.{arbitrary => arb}
import org.scalacheck.{Arbitrary, Gen}

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances extends kantan.csv.laws.discipline.ArbitraryArities {
  val csv: Gen[List[List[String]]] = arb[List[List[Cell]]].map(_.map(_.map(_.value)))



  // - Errors ----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  val genOutOfBoundsError: Gen[DecodeError.OutOfBounds] = for(i ← Gen.posNum[Int]) yield DecodeError.OutOfBounds(i)
  val genTypeError: Gen[DecodeError.TypeError] = Gen.const(DecodeError.TypeError(new Exception()))
  val genDecodeError: Gen[DecodeError] = Gen.oneOf(genOutOfBoundsError, genTypeError)

  val genIOError: Gen[ParseError.IOError] = Gen.const(ParseError.IOError(new IOException()))
  val genSyntaxError: Gen[ParseError.SyntaxError] = for {
    i ← arb[Int]
    j ← arb[Int]
  } yield ParseError.SyntaxError(i, j)
  val genParseError: Gen[ParseError] = Gen.oneOf(genIOError, genSyntaxError)

  implicit val arbDecodeError: Arbitrary[DecodeError] = Arbitrary(genDecodeError)
  implicit val arbParseError: Arbitrary[ParseError] = Arbitrary(genParseError)
  implicit val arbReadError: Arbitrary[ReadError] = Arbitrary(Gen.oneOf(genDecodeError, genParseError))



  // - Encoders and decoders -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbCellDecoder[A: Arbitrary]: Arbitrary[CellDecoder[A]] =
    Arbitrary(arb[String ⇒ DecodeResult[A]].map(f ⇒ CellDecoder(f)))

  implicit def arbCellEncoder[A: Arbitrary]: Arbitrary[CellEncoder[A]] =
    Arbitrary(arb[A ⇒ String].map(f ⇒ CellEncoder(f)))

  implicit def arbRowDecoder[A: Arbitrary]: Arbitrary[RowDecoder[A]] =
    Arbitrary(arb[Seq[String] ⇒ DecodeResult[A]].map(f ⇒ RowDecoder(f)))

  implicit def arbRowEncoder[A: Arbitrary]: Arbitrary[RowEncoder[A]] =
    Arbitrary(arb[A ⇒ Seq[String]].map(f ⇒ RowEncoder(f)))


  // - Codec values ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbLegalOptionRow[D](implicit dl: Arbitrary[LegalRow[D]]): Arbitrary[LegalRow[Option[D]]] =
    Arbitrary(genLegalOption[Seq[String], D](_.isEmpty)(Seq.empty))

  implicit def arbIllegalOptionRow[D](implicit dl: Arbitrary[IllegalRow[D]]): Arbitrary[IllegalRow[Option[D]]] =
    Arbitrary(genIllegalOption[Seq[String], D](_.isEmpty))
}
