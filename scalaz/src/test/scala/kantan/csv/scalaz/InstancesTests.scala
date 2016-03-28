package kantan.csv.scalaz

import _root_.scalaz.scalacheck.ScalazProperties.equal
import kantan.csv._
import kantan.csv.scalaz.arbitrary._


class InstancesTests extends ScalazSuite {
  checkAll("ReadError", equal.laws[ReadError])
  checkAll("DecodeError", equal.laws[DecodeError])
  checkAll("ParseError", equal.laws[ParseError])
}
