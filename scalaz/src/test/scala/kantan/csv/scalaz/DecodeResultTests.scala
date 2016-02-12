package kantan.csv.scalaz

import kantan.csv.DecodeResult
import kantan.csv.laws.discipline.arbitrary._

import scalaz.scalacheck.ScalazProperties.{equal, monad}
import scalaz.std.anyVal._

class DecodeResultTests extends ScalazSuite {
  checkAll("DecodeResult", monad.laws[DecodeResult])
  checkAll("DecodeResult[Int]", equal.laws[DecodeResult[Int]])
}
