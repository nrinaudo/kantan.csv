package tabulate.interop.scalaz

import export.{export, exports}
import tabulate.ops._
import tabulate.{CellEncoder, RowEncoder}

import scalaz._

@exports
object RowEncoders {
  @export(Instantiated)
  implicit def foldableRowEncoder[A: CellEncoder, F[_]: Foldable]: RowEncoder[F[A]] =
    RowEncoder(as => Foldable[F].foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += a.asCsvCell).result())

  @export(Instantiated)
  implicit def eitherRowEncoder[A: RowEncoder, B: RowEncoder]: RowEncoder[A \/ B] =
    RowEncoder(a => a match {
      case -\/(a)  => a.asCsvRow
      case \/-(b)  => b.asCsvRow
    })
}
