package tabulate.interop.cats

import cats.Foldable
import cats.data.Xor
import export.exports
import tabulate.{CellEncoder, RowEncoder}
import tabulate.ops._

trait DerivedRowEncoder[A] extends RowEncoder[A]

@exports
object DerivedRowEncoder {
  implicit def xorRowEncoder[A: RowEncoder, B: RowEncoder]: RowEncoder[Xor[A, B]] = RowEncoder(eab => eab match {
    case Xor.Left(a)  => a.asCsvRow
    case Xor.Right(b)  => b.asCsvRow
  })

  implicit def foldableRowEncoder[A: CellEncoder, F[_]: Foldable]: RowEncoder[F[A]] = new RowEncoder[F[A]] {
    override def encode(as: F[A]): Seq[String] = Foldable[F].foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += a.asCsvCell).result()
  }
}
