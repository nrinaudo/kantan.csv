package tabulate.scalaz

import tabulate.{DecodeResult, RowDecoder}
import tabulate.laws.discipline.arbitrary
import arbitrary._
import tabulate.laws.discipline.equality

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.monad
import scalaz.std.anyVal._

class RowDecoderTests extends ScalazSuite {
  implicit def rowDecoderEq[A: Equal]: Equal[RowDecoder[A]] = new Equal[RowDecoder[A]] {
    override def equal(a1: RowDecoder[A], a2: RowDecoder[A]): Boolean =
      equality.rowDecoder(a1, a2)(Equal[DecodeResult[A]].equal)
  }

  checkAll("RowDecoder", monad.laws[RowDecoder])
}