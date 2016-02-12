package kantan.csv.scalaz

import export.{export, exports}
import kantan.csv
import kantan.csv.CellEncoder

import scalaz._

@exports
object RowEncoders {
  @export(Orphan)
  implicit def eitherRowEncoder[A, B](implicit ea: csv.RowEncoder[A], eb: csv.RowEncoder[B]): csv.RowEncoder[A \/ B] =
    csv.RowEncoder(eab ⇒ eab match {
      case -\/(a)  ⇒ ea.encode(a)
      case \/-(b)  ⇒ eb.encode(b)
    })

  @export(Orphan)
  implicit def foldableRowEncoder[A, F[_]](implicit ea: CellEncoder[A], F: Foldable[F]): csv.RowEncoder[F[A]] =
    csv.RowEncoder(as ⇒ F.foldLeft(as, Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result())

  @export(Orphan)
  implicit def maybeEncoder[A](implicit ea: csv.RowEncoder[A]): csv.RowEncoder[Maybe[A]] = new csv.RowEncoder[Maybe[A]] {
    override def encode(a: Maybe[A]) = a.map(ea.encode).getOrElse(Seq.empty)
  }
}
