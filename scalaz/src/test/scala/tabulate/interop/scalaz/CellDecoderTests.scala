package tabulate.interop.scalaz

import tabulate.{DecodeResult, CellDecoder}
import tabulate.laws.discipline.{arbitrary, equality}
import arbitrary._
import tabulate.laws.discipline.equality

import scalaz.Equal
import scalaz.scalacheck.ScalazProperties.monad
import scalaz.std.anyVal._

class CellDecoderTests extends ScalazSuite {
  implicit def cellDecoderEq[A: Equal]: Equal[CellDecoder[A]] = new Equal[CellDecoder[A]] {
    override def equal(a1: CellDecoder[A], a2: CellDecoder[A]): Boolean =
      equality.cellDecoder(a1, a2)(Equal[DecodeResult[A]].equal)
  }

  checkAll("CellDecoder", monad.laws[CellDecoder])
}