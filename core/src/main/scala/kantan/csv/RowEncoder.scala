package kantan.csv

import kantan.codecs.Encoder

object RowEncoder extends GeneratedRowEncoders {
  def apply[A](implicit ea: RowEncoder[A]): RowEncoder[A] = ea
  def apply[A](f: A ⇒ Seq[String]): RowEncoder[A] = Encoder(f)
}

trait RowEncoderInstances {
  implicit def fromCellEncoder[A](implicit ea: CellEncoder[A]): RowEncoder[A] = RowEncoder(a ⇒ Seq(ea.encode(a)))

  implicit def traversable[A, M[X] <: TraversableOnce[X]](implicit ea: CellEncoder[A]): RowEncoder[M[A]] =
    RowEncoder { as ⇒ as.foldLeft(Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result() }

  implicit def eitherRowEncoder[A, B](implicit ea: RowEncoder[A], eb: RowEncoder[B]): RowEncoder[Either[A, B]] =
    RowEncoder { ss ⇒ ss match {
      case Left(a) ⇒ ea.encode(a)
      case Right(b) ⇒ eb.encode(b)
    }}

  implicit def optionRowEncoder[A](implicit ea: RowEncoder[A]): RowEncoder[Option[A]] =
    RowEncoder(_.map(a ⇒ ea.encode(a)).getOrElse(Seq.empty))
}
