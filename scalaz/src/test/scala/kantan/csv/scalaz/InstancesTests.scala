package kantan.csv.scalaz

import kantan.csv._
import kantan.csv.scalaz.arbitrary._

import _root_.scalaz.scalacheck.ScalazProperties.{contravariant, equal, functor}
import _root_.scalaz.std.anyVal._


class InstancesTests extends ScalazSuite {
  checkAll("CsvError", equal.laws[CsvError])
  checkAll("DecodeError", equal.laws[DecodeError])
}
