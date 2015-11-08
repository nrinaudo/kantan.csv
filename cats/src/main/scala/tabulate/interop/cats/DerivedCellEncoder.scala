package tabulate.interop.cats

import cats.data.Xor
import export.exports
import tabulate.CellEncoder
import tabulate.ops._

trait DerivedCellEncoder[A] extends CellEncoder[A]

@exports
object DerivedCellEncoder {
  implicit def xorCellEncoder[A: CellEncoder, B: CellEncoder]: DerivedCellEncoder[Xor[A, B]] =
  new DerivedCellEncoder[Xor[A, B]] {
    override def encode(a: Xor[A, B]) = a match {
      case Xor.Left(a)  => a.asCsvCell
      case Xor.Right(b) => b.asCsvCell
    }
  }
}
