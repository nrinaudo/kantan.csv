package tabulate.interop.scalaz

import export.exports
import tabulate.CellEncoder
import tabulate.ops._

import scalaz.{Maybe, \/-, -\/, \/}

trait DerivedCellEncoder[A] extends CellEncoder[A]

@exports
object DerivedCellEncoder {
  implicit def eitherCellEncoder[A: CellEncoder, B: CellEncoder]: CellEncoder[A \/ B] = CellEncoder(eab => eab match {
    case -\/(a)  => a.asCsvCell
    case \/-(b)  => b.asCsvCell
  })

  implicit def maybeEncoder[A: CellEncoder]: CellEncoder[Maybe[A]] =
    CellEncoder(ma => ma.map(CellEncoder[A].encode).getOrElse(""))
}
