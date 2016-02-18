package kantan.csv

import kantan.codecs.Encoder
import simulacrum.{noop, op, typeclass}

@typeclass trait RowEncoder[A] extends Encoder[Seq[String], A, RowEncoder] { self ⇒
  @op("asCsvRow")
  def encode(a: A): Seq[String]

  override protected def copy[DD](f: DD => Seq[String]) = RowEncoder(f)
}

@export.imports[RowEncoder]
trait LowPriorityRowEncoders {
  implicit def traversable[A, M[X] <: TraversableOnce[X]](implicit ea: CellEncoder[A]): RowEncoder[M[A]] =
    RowEncoder { as ⇒ as.foldLeft(Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result() }

  implicit def cellEncoder[A](implicit ea: CellEncoder[A]): RowEncoder[A] = RowEncoder(a ⇒ Seq(ea.encode(a)))
}

object RowEncoder extends LowPriorityRowEncoders with GeneratedRowEncoders {
  def apply[A](f: A ⇒ Seq[String]): RowEncoder[A] = new RowEncoder[A] {
    override def encode(a: A) = f(a)
  }

  /** Specialised encoder for sequences of strings: these do not need to be modified. */
  implicit def strSeq[M[X] <: Seq[X]]: RowEncoder[M[String]] = RowEncoder(ss ⇒ ss)

  implicit def either[A, B](implicit ea: RowEncoder[A], eb: RowEncoder[B]): RowEncoder[Either[A, B]] =
    RowEncoder { ss ⇒ ss match {
      case Left(a) ⇒ ea.encode(a)
      case Right(b) ⇒ eb.encode(b)
    }}

  implicit def option[A](implicit ea: RowEncoder[A]): RowEncoder[Option[A]] =
    RowEncoder(_.map(a ⇒ ea.encode(a)).getOrElse(Seq.empty))

}
