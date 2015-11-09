package tabulate.interop.cats

import cats.Foldable
import cats.data.Xor
import export.{export, exports}
import tabulate.ops._
import tabulate.{CellEncoder, RowEncoder}

@exports
object RowEncoders {
  @export(Instantiated)
  implicit def xorRowEncoder[A: RowEncoder, B: RowEncoder]: RowEncoder[Xor[A, B]] =
    RowEncoder(a => a match {
      case Xor.Left(a)  => a.asCsvRow
      case Xor.Right(b)  => b.asCsvRow
    })

  @export(Instantiated)
  implicit def foldableRowEncoder[A: CellEncoder, F[_]: Foldable]: RowEncoder[F[A]] =
    RowEncoder(as => Foldable[F].foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += a.asCsvCell).result())
}
