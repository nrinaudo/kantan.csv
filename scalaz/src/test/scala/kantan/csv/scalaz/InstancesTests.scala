package kantan.csv.scalaz

import kantan.csv._
import kantan.csv.scalaz.arbitrary._
import kantan.csv.scalaz.equality._

import _root_.scalaz.scalacheck.ScalazProperties.{contravariant, equal, functor}
import _root_.scalaz.std.anyVal._


class InstancesTests extends ScalazSuite {
  checkAll("CsvError", equal.laws[CsvError])
  checkAll("DecodeError", equal.laws[DecodeError])
  checkAll("CellDecoder", functor.laws[CellDecoder])
  checkAll("RowDecoder", functor.laws[RowDecoder])
  checkAll("CellEncoder", contravariant.laws[CellEncoder])
  //checkAll("RowEncoder", contravariant.laws[RowEncoder])
}
