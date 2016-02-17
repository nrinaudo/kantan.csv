package kantan.csv.scalaz

import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.laws.discipline.equality
import kantan.csv.{CellDecoder, DecodeResult}

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.functor
import scalaz.std.anyVal._

class CellDecoderTests extends ScalazSuite {
  implicit def cellDecoderEq[A: Equal]: Equal[CellDecoder[A]] = new Equal[CellDecoder[A]] {
    override def equal(a1: CellDecoder[A], a2: CellDecoder[A]): Boolean =
      equality.cellDecoder(a1, a2)(Equal[DecodeResult[A]].equal)
  }

  checkAll("CellDecoder", functor.laws[CellDecoder])
}