package kantan.csv.cats

import cats.data.Xor
import export.{export, exports}
import kantan.csv.CellDecoder

@exports
object CellDecoders {
  @export(Orphan)
  implicit def xorCellDecoder[A, B](implicit da: CellDecoder[A], db: CellDecoder[B]): CellDecoder[Xor[A, B]] =
    CellDecoder(s ⇒ da.decode(s).map(a ⇒ Xor.Left(a)).orElse(db.decode(s).map(b ⇒ Xor.Right(b))))
}
