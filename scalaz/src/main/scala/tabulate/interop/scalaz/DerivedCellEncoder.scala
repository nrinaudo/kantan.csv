package tabulate.interop.scalaz

import export.exports
import tabulate.CellEncoder
import tabulate.ops._

import scalaz.{-\/, Maybe, \/, \/-}

trait DerivedCellEncoder[A] extends CellEncoder[A]

@exports
object DerivedCellEncoder {
  implicit def eitherCellEncoder[A: CellEncoder, B: CellEncoder]: DerivedCellEncoder[A \/ B] =
  new DerivedCellEncoder[\/[A, B]] {
    override def encode(eab: \/[A, B]) = eab match {
        case -\/(a)  => a.asCsvCell
        case \/-(b)  => b.asCsvCell
      }
  }

  implicit def maybeEncoder[A: CellEncoder]: DerivedCellEncoder[Maybe[A]] =
    new DerivedCellEncoder[Maybe[A]] {
      override def encode(a: Maybe[A]) = a.map(CellEncoder[A].encode).getOrElse("")
    }
}
