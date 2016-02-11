package tabulate.interop.cats

import cats.data.Xor
import export.{export, exports}
import tabulate.RowDecoder

@exports
object RowDecoders {
  @export(Orphan)
  implicit def xorRowDecoder[A, B](implicit da: RowDecoder[A], db: RowDecoder[B]): RowDecoder[Xor[A, B]] =
    RowDecoder(row ⇒ da.decode(row).map(a ⇒ Xor.Left(a)).orElse(db.decode(row).map(b ⇒ Xor.Right(b))))
}
