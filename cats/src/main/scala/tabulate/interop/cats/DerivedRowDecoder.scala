package tabulate.interop.cats

import cats.data.Xor
import export.exports
import tabulate.RowDecoder

trait DerivedRowDecoder[A] extends RowDecoder[A]

@exports
object DerivedRowDecoder {
  implicit def xorRowDecoder[A: RowDecoder, B: RowDecoder]: RowDecoder[Xor[A, B]] =
    RowDecoder { s => RowDecoder[A].decode(s).map(a => Xor.Left(a))
      .orElse(RowDecoder[B].decode(s).map(b => Xor.Right(b)))
    }
}
