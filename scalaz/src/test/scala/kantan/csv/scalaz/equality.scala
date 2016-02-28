package kantan.csv.scalaz

import _root_.scalaz.std.string._
import kantan.codecs.scalaz.laws._
import kantan.csv.{CellEncoder, RowDecoder, CellDecoder, DecodeError}
import org.scalacheck.Arbitrary

import scalaz.Equal

/** All non-standard Equal instances required for testing. */
object equality {
  // TODO: needs Eq[Seq[String]]
  implicit def cellDecoderEqual[D: Equal] = decoderEqual[String, D, DecodeError, CellDecoder]
  implicit def rowDecoderEqual[D: Equal] = decoderEqual[Seq[String], D, DecodeError, RowDecoder]
  implicit def cellEncoderEqual[D: Arbitrary] = encoderEqual[String, D, CellEncoder]
  //implicit def rowEncoderEqual[D: Arbitrary] = encoderEqual[Seq[String], D, RowEncoder]
}
