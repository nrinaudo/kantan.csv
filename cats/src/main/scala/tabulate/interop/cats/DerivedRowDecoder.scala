package tabulate.interop.cats

import cats.data.Xor
import export.exports
import tabulate.{DecodeResult, RowDecoder}

trait DerivedRowDecoder[A] extends RowDecoder[A]

@exports
object DerivedRowDecoder {
  implicit def xorRowDecoder[A: RowDecoder, B: RowDecoder]: DerivedRowDecoder[Xor[A, B]] =
    new DerivedRowDecoder[Xor[A, B]] {
      override def decode(row: Seq[String]): DecodeResult[Xor[A, B]] =
        RowDecoder[A].decode(row).map(a => Xor.Left(a))
          .orElse(RowDecoder[B].decode(row).map(b => Xor.Right(b)))
    }
}
