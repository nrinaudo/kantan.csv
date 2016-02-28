package kantan.csv.cats

import cats._
import cats.std.all._
import kantan.codecs.cats.laws._
import kantan.csv.{CellEncoder, RowDecoder, CellDecoder, DecodeError}
import org.scalacheck.Arbitrary

/** All non-standard Eq instances required for testing. */
object equality {
  // TODO: needs Eq[Seq[String]]

  implicit def cellDecoderEq[D: Eq] = decoderEq[String, D, DecodeError, CellDecoder]
  implicit def rowDecoderEq[D: Eq] = decoderEq[Seq[String], D, DecodeError, RowDecoder]
  implicit def cellEncoderEq[D: Arbitrary] = encoderEq[String, D, CellEncoder]
  //implicit def rowEncoderEq[D: Arbitrary] = encoderEq[Seq[String], D, RowEncoder]
}
