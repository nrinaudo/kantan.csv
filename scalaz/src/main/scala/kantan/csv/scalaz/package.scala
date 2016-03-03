package kantan.csv

import kantan.csv

import _root_.scalaz.Maybe._
import _root_.scalaz._, Scalaz._

/** Declares various type class instances for bridging `kantan.csv` and `scalaz`. */
package object scalaz extends kantan.codecs.scalaz.ScalazInstances {
  implicit def eitherCellDecoder[A, B](implicit da: CellDecoder[A], db: CellDecoder[B]): CellDecoder[A \/ B] =
    CellDecoder(s ⇒ da.decode(s).map(_.left[B]).orElse(db.decode(s).map(_.right[A])))

  implicit def maybeCellDecoder[A](implicit da: CellDecoder[A]): CellDecoder[Maybe[A]] = CellDecoder { s ⇒
    if(s.isEmpty) DecodeResult.success(empty)
    else          da.decode(s).map(just)
  }

  implicit def eitherCellEncoder[A, B](implicit ea: CellEncoder[A], eb: CellEncoder[B]): CellEncoder[A \/ B] =
    new CellEncoder[\/[A, B]] {
      override def encode(eab: \/[A, B]) = eab match {
        case -\/(a)  ⇒ ea.encode(a)
        case \/-(b)  ⇒ eb.encode(b)
      }
    }

  implicit def maybeCellEncoder[A](implicit ea: CellEncoder[A]): CellEncoder[Maybe[A]] = new CellEncoder[Maybe[A]] {
    override def encode(a: Maybe[A]) = a.map(ea.encode).getOrElse("")
  }

  implicit def eitherRowDecoder[A, B](implicit da: csv.RowDecoder[A], db: csv.RowDecoder[B]): csv.RowDecoder[A \/ B] =
    csv.RowDecoder(row ⇒ da.decode(row).map(_.left[B]).orElse(db.decode(row).map(_.right[A])))

  implicit def maybeRowDecoder[A](implicit da: csv.RowDecoder[A]): csv.RowDecoder[Maybe[A]] = csv.RowDecoder { row ⇒
    if(row.isEmpty) DecodeResult.success(empty)
    else            da.decode(row).map(just)
  }

  implicit def eitherRowEncoder[A, B](implicit ea: csv.RowEncoder[A], eb: csv.RowEncoder[B]): csv.RowEncoder[A \/ B] =
    csv.RowEncoder(eab ⇒ eab match {
      case -\/(a)  ⇒ ea.encode(a)
      case \/-(b)  ⇒ eb.encode(b)
    })

  implicit def foldableRowEncoder[A, F[_]](implicit ea: CellEncoder[A], F: Foldable[F]): csv.RowEncoder[F[A]] =
    csv.RowEncoder(as ⇒ F.foldLeft(as, Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result())

  implicit def maybeRowEncoder[A](implicit ea: csv.RowEncoder[A]): csv.RowEncoder[Maybe[A]] = new csv.RowEncoder[Maybe[A]] {
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
