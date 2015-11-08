package tabulate.interop.cats

import cats.data.Xor
import export.exports
import tabulate.CellDecoder

trait DerivedCellDecoder[A] extends CellDecoder[A]

@exports
object DerivedCellDecoder {
  implicit def xorCellDecoder[A: CellDecoder, B: CellDecoder]: CellDecoder[Xor[A, B]] =
    CellDecoder { s => CellDecoder[A].decode(s).map(a => Xor.Left(a))
      .orElse(CellDecoder[B].decode(s).map(b => Xor.Right(b)))
    }
}
