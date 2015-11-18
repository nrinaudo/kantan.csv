package tabulate.interop.cats

import cats.data.Xor
import export.{export, exports}
import tabulate.CellDecoder

@exports
object CellDecoders {
  @export(Instantiated)
  implicit def xorCellDecoder[A, B](implicit da: CellDecoder[A], db: CellDecoder[B]): CellDecoder[Xor[A, B]] =
    CellDecoder(s => da.decode(s).map(a => Xor.Left(a)).orElse(db.decode(s).map(b => Xor.Right(b))))
}
