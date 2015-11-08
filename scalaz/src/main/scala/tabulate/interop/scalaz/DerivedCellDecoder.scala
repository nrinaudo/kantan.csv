package tabulate.interop.scalaz

import export.exports
import tabulate.{DecodeResult, CellDecoder}

import scalaz.Maybe._
import scalaz.{Maybe, Scalaz, \/}, Scalaz._

trait DerivedCellDecoder[A] extends CellDecoder[A]

@exports
object DerivedCellDecoder {
  implicit def eitherCellDecoder[A: CellDecoder, B: CellDecoder]: DerivedCellDecoder[A \/ B] =
    new DerivedCellDecoder[\/[A, B]] {
      override def decode(s: String) = CellDecoder[A].decode(s).map(_.left[B])
        .orElse(CellDecoder[B].decode(s).map(_.right[A]))
  }

  implicit def maybeDecoder[A: CellDecoder]: DerivedCellDecoder[Maybe[A]] =
  new DerivedCellDecoder[Maybe[A]] {
    override def decode(s: String) =
      if(s.isEmpty) DecodeResult.success(empty)
      else          CellDecoder[A].decode(s).map(just)
  }
}
