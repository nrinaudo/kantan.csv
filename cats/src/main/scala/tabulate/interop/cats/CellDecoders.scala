package tabulate.interop.cats

import cats.data.Xor
import export.{export, exports}
import tabulate.{DecodeResult, CellDecoder}

@exports
object CellDecoders {
  @export(Instantiated)
  implicit def xorCellDecoder[A: CellDecoder, B: CellDecoder]: CellDecoder[Xor[A, B]] =
    CellDecoder(s => CellDecoder[A].decode(s).map(a => Xor.Left(a))
      .orElse(CellDecoder[B].decode(s).map(b => Xor.Right(b))))
}
