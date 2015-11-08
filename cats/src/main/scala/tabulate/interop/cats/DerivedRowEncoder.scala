package tabulate.interop.cats

import cats.Foldable
import cats.data.Xor
import export.exports
import tabulate.ops._
import tabulate.{CellEncoder, RowEncoder}

trait DerivedRowEncoder[A] extends RowEncoder[A]

@exports
object DerivedRowEncoder {
  implicit def xorRowEncoder[A: RowEncoder, B: RowEncoder]: DerivedRowEncoder[Xor[A, B]] =
  new DerivedRowEncoder[Xor[A, B]] {
    override def encode(a: Xor[A, B]): Seq[String] = a match {
        case Xor.Left(a)  => a.asCsvRow
        case Xor.Right(b)  => b.asCsvRow
      }
  }

  implicit def foldableRowEncoder[A: CellEncoder, F[_]: Foldable]: DerivedRowEncoder[F[A]] = new DerivedRowEncoder[F[A]] {
    override def encode(as: F[A]): Seq[String] = Foldable[F].foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += a.asCsvCell).result()
  }
}
