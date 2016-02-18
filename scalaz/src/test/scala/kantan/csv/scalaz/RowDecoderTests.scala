package kantan.csv.scalaz

import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.equality
import kantan.csv.{CsvResult, RowDecoder}

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.functor
import scalaz.std.anyVal._

class RowDecoderTests extends ScalazSuite {
  implicit def rowDecoderEq[A: Equal]: Equal[RowDecoder[A]] = new Equal[RowDecoder[A]] {
    override def equal(a1: RowDecoder[A], a2: RowDecoder[A]): Boolean =
      equality.rowDecoder(a1, a2)(Equal[CsvResult[A]].equal)
  }

  checkAll("RowDecoder", functor.laws[RowDecoder])
}