package kantan.csv.cats

import cats.Foldable
import cats.data.Xor
import export.{export, exports}
import kantan.csv
import kantan.csv.CellEncoder

@exports
object RowEncoders {
  @export(Orphan)
  implicit def xorRowEncoder[A, B](implicit ea: csv.RowEncoder[A], eb: csv.RowEncoder[B]): csv.RowEncoder[Xor[A, B]] =
    csv.RowEncoder(xab ⇒ xab match {
      case Xor.Left(a)  ⇒ ea.encode(a)
      case Xor.Right(b) ⇒ eb.encode(b)
    })

  @export(Orphan)
  implicit def foldableRowEncoder[A, F[_]](implicit ea: CellEncoder[A], F: Foldable[F]): csv.RowEncoder[F[A]] =
    csv.RowEncoder(as ⇒ F.foldLeft(as, Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result())
}
