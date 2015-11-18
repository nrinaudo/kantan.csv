package tabulate.interop.cats

import cats.data.Xor
import export.{export, exports}
import tabulate.CellEncoder

@exports
object CellEncoders {
  @export(Instantiated)
  implicit def xorCellEncoder[A, B](implicit ea: CellEncoder[A], eb: CellEncoder[B]): CellEncoder[Xor[A, B]] =
    CellEncoder(eab => eab match {
      case Xor.Left(a)  => ea.encode(a)
      case Xor.Right(b) => eb.encode(b)
    })
}
