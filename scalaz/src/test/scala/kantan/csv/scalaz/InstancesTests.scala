package kantan.csv.scalaz

import _root_.scalaz.scalacheck.ScalazProperties.equal
import kantan.csv._
import kantan.csv.scalaz.arbitrary._


class InstancesTests extends ScalazSuite {
  checkAll("CsvError", equal.laws[CsvError])
  checkAll("DecodeError", equal.laws[DecodeError])
}
