package kantan.csv.cats

import cats.data.Xor
import export.{export, exports}
import kantan.csv.CellEncoder

@exports
object CellEncoders {
  @export(Orphan)
  implicit def xorCellEncoder[A, B](implicit ea: CellEncoder[A], eb: CellEncoder[B]): CellEncoder[Xor[A, B]] =
    CellEncoder(eab ⇒ eab match {
      case Xor.Left(a)  ⇒ ea.encode(a)
      case Xor.Right(b) ⇒ eb.encode(b)
    })
}
