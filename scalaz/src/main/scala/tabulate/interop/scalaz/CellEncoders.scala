package tabulate.interop.scalaz

import export.{export, exports}
import tabulate.CellEncoder
import tabulate.ops._

import scalaz.{-\/, Maybe, \/, \/-}

@exports
object CellEncoders {
  @export(Instantiated)
  implicit def eitherCellEncoder[A: CellEncoder, B: CellEncoder]: CellEncoder[A \/ B] =
  new CellEncoder[\/[A, B]] {
    override def encode(eab: \/[A, B]) = eab match {
        case -\/(a)  => a.asCsvCell
        case \/-(b)  => b.asCsvCell
      }
  }

  @export(Instantiated)
  implicit def maybeEncoder[A: CellEncoder]: CellEncoder[Maybe[A]] =
    new CellEncoder[Maybe[A]] {
      override def encode(a: Maybe[A]) = a.map(CellEncoder[A].encode).getOrElse("")
    }
}
