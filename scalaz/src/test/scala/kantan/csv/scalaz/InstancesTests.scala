package kantan.csv.scalaz

import kantan.codecs.scalaz.laws._
import arbitrary._
import kantan.csv._
import org.scalacheck.Arbitrary

import _root_.scalaz.Equal
import _root_.scalaz.scalacheck.ScalazProperties.{contravariant, equal, functor}
import _root_.scalaz.std.anyVal._
import _root_.scalaz.std.string._


class InstancesTests extends ScalazSuite {
  // TODO: needs Eq[Seq[String]]

  implicit def cellDecoderEqual[D: Equal] = decoderEqual[String, D, DecodeError, CellDecoder]
  implicit def rowDecoderEqual[D: Equal] = decoderEqual[Seq[String], D, DecodeError, RowDecoder]
  implicit def cellEncoderEqual[D: Arbitrary] = encoderEqual[String, D, CellEncoder]
  //implicit def rowEncoderEqual[D: Arbitrary] = encoderEqual[Seq[String], D, RowEncoder]

  checkAll("CsvError", equal.laws[CsvError])
  checkAll("DecodeError", equal.laws[DecodeError])
  checkAll("CellDecoder", functor.laws[CellDecoder])
  checkAll("RowDecoder", functor.laws[RowDecoder])
  checkAll("CellEncoder", contravariant.laws[CellEncoder])
  //checkAll("RowEncoder", contravariant.laws[RowEncoder])
}
