package tabulate.interop.cats

import cats.data.Xor
import export.{export, exports}
import tabulate.CellEncoder
import tabulate.ops._

@exports
object CellEncoders {
  @export(Instantiated)
  implicit def xorCellEncoder[A: CellEncoder, B: CellEncoder]: CellEncoder[Xor[A, B]] =
  CellEncoder(a => a match {
    case Xor.Left(a)  => a.asCsvCell
    case Xor.Right(b) => b.asCsvCell
  })
}
