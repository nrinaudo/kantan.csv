package tabulate.interop.cats

import cats.data.Xor
import export.exports
import tabulate.{DecodeResult, CellDecoder}

trait DerivedCellDecoder[A] extends CellDecoder[A]

@exports
object DerivedCellDecoder {
  implicit def xorCellDecoder[A: CellDecoder, B: CellDecoder]: DerivedCellDecoder[Xor[A, B]] =
    new DerivedCellDecoder[Xor[A, B]] {
      override def decode(s: String): DecodeResult[Xor[A, B]] = CellDecoder[A].decode(s).map(a => Xor.Left(a))
        .orElse(CellDecoder[B].decode(s).map(b => Xor.Right(b)))
    }
}
