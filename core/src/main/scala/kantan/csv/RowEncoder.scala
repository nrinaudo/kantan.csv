package kantan.csv

import kantan.codecs.Encoder

/** Provides various instance creation and summoning methods.
  *
  * Of particular interest are the instance creation functions. There are four main families, depending on the type
  * to encode:
  *
  *  - [[encoder1 encoderXXX]]: creates encoders from a function of arity `XXX` and for which you need to specify
  *    a mapping ''row index to parameter'' (such as if you need to skip some CSV cells, for instance).
  *  - [[ordered1 orderedXXX]]: create encoders from a function of arity `XXX` such that its parameters are organised
  *    in exactly the same way as CSV rows.
  *  - [[caseEncoder1 caseEncoderXXX]]: specialisation of [[encoder1 encoderXXX]] for case classes.
  *  - [[caseOrdered1 caseOrderedXXX]]: specialisation of [[ordered1 orderedXXX]] for case classes.
  *
  * Note that a lot of types already have implicit instances: tuples, collections... moreover, the `generics` module
  * can automatically derive valid instances for a lot of common scenarios.
  */
object RowEncoder extends GeneratedRowEncoders {
  /** Summons an implicit instance of [[RowEncoder]] for the desired type if one can be found.
    *
    * This is essentially a shorter way of calling `implicitly[RowEncoder[A]]`.
    */
  def apply[A](implicit ea: RowEncoder[A]): RowEncoder[A] = ea

  /** Creates a new [[RowEncoder]] using the specified function for encoding. */
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
