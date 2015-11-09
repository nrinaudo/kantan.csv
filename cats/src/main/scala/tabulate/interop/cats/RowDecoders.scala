package tabulate.interop.cats

import cats.data.Xor
import export.{export, exports}
import tabulate.RowDecoder

@exports
object RowDecoders {
  @export(Instantiated)
  implicit def xorRowDecoder[A: RowDecoder, B: RowDecoder]: RowDecoder[Xor[A, B]] =
    RowDecoder(row => RowDecoder[A].decode(row).map(a => Xor.Left(a))
          .orElse(RowDecoder[B].decode(row).map(b => Xor.Right(b))))
}
