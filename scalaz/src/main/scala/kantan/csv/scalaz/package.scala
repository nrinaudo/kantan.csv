package kantan.csv

import _root_.scalaz.Maybe._
import _root_.scalaz._
import Scalaz._

/** Declares various type class instances for bridging `kantan.csv` and `scalaz`. */
package object scalaz extends kantan.codecs.scalaz.ScalazInstances {
  implicit def eitherRowDecoder[A, B](implicit da: RowDecoder[A], db: RowDecoder[B]): RowDecoder[A \/ B] =
    RowDecoder(row ⇒ da.decode(row).map(_.left[B]).orElse(db.decode(row).map(_.right[A])))

  implicit def maybeRowDecoder[A](implicit da: RowDecoder[A]): RowDecoder[Maybe[A]] = RowDecoder { row ⇒
    if(row.isEmpty) DecodeResult.success(empty)
    else            da.decode(row).map(just)
  }

  implicit def eitherRowEncoder[A, B](implicit ea: RowEncoder[A], eb: RowEncoder[B]): RowEncoder[A \/ B] =
    RowEncoder(_.fold(ea.encode, eb.encode))

  implicit def foldableRowEncoder[A, F[_]](implicit ea: CellEncoder[A], F: Foldable[F]): RowEncoder[F[A]] =
    RowEncoder(as ⇒ F.foldLeft(as, Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result())

  implicit def maybeRowEncoder[A](implicit ea: RowEncoder[A]): RowEncoder[Maybe[A]] = new RowEncoder[Maybe[A]] {
    override def encode(a: Maybe[A]) = a.map(ea.encode).getOrElse(Seq.empty)
  }



  // - CSV input / output ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Contravariant` instance for `CsvInput`. */
  implicit val csvInput: Contravariant[CsvInput] = new Contravariant[CsvInput] {
    override def contramap[A, B](r: CsvInput[A])(f: B ⇒ A) = r.contramap(f)
  }

  /** `Contravariant` instance for `CsvOutput`. */
  implicit val csvOutput: Contravariant[CsvOutput] = new Contravariant[CsvOutput] {
    override def contramap[A, B](r: CsvOutput[A])(f: B ⇒ A) = r.contramap(f)
  }



  // - CsvError --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val csvErrorEqual: Equal[CsvError] = new Equal[CsvError] {
    override def equal(a1: CsvError, a2: CsvError): Boolean = a1 == a2
  }

  implicit val decodeErrorEqual: Equal[DecodeError] = new Equal[DecodeError] {
    override def equal(a1: DecodeError, a2: DecodeError): Boolean = a1 == a2
  }
}
