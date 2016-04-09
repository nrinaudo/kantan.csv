package kantan.csv

import _root_.cats._
import _root_.cats.data.Xor
import _root_.cats.functor.Contravariant
import kantan.csv

/** Declares various type class instances for bridging `kantan.csv` and `cats`. */
package object cats extends kantan.codecs.cats.CatsInstances {
  implicit def xorRowDecoder[A, B](implicit da: RowDecoder[A], db: RowDecoder[B]): RowDecoder[Xor[A, B]] =
    RowDecoder(row ⇒ da.decode(row).map(a ⇒ Xor.Left(a)).orElse(db.decode(row).map(b ⇒ Xor.Right(b))))

  implicit def xorRowEncoder[A, B](implicit ea: csv.RowEncoder[A], eb: csv.RowEncoder[B]): csv.RowEncoder[Xor[A, B]] =
    csv.RowEncoder(_.fold(ea.encode, eb.encode))

  implicit def foldableRowEncoder[F[_], A](implicit ea: CellEncoder[A], F: Foldable[F]): RowEncoder[F[A]] =
    csv.RowEncoder(as ⇒ F.foldLeft(as, Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result())



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


  // - ReadError -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val readErrorEq: Eq[ReadError] = new Eq[ReadError] {
    override def eqv(x: ReadError, y: ReadError): Boolean = x == y
  }

  implicit val decodeErrorEq: Eq[DecodeError] = new Eq[DecodeError] {
    override def eqv(x: DecodeError, y: DecodeError): Boolean = x == y
  }

  implicit val parseErrorEq: Eq[ParseError] = new Eq[ParseError] {
    override def eqv(x: ParseError, y: ParseError): Boolean = x == y
  }
}
