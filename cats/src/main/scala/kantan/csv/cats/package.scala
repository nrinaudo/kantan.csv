package kantan.csv

import _root_.cats._
import _root_.cats.functor.Contravariant

/** Declares various type class instances for bridging `kantan.csv` and `cats`. */
package object cats extends kantan.codecs.cats.CatsInstances {
  // - CellCodec -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Functor` instance for `CellDecoder`. */
  implicit val cellDecoder = new Functor[CellDecoder] {
    override def map[A, B](fa: CellDecoder[A])(f: A ⇒ B) = fa.map(f)
  }

  /** `Contravariant` instance for `CellEncoder`. */
  implicit val cellEncoder = new Contravariant[CellEncoder] {
    override def contramap[A, B](fa: CellEncoder[A])(f: B ⇒ A) = fa.contramap(f)
  }



  // - RowCodec --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Functor` instance for `RowDecoder`. */
  implicit val rowDecoder = new Functor[RowDecoder] {
    override def map[A, B](fa: RowDecoder[A])(f: A ⇒ B) = fa.map(f)
  }

  /** `Contravariant` instance for `RowEncoder`. */
  implicit val rowEncoder = new Contravariant[RowEncoder] {
    override def contramap[A, B](fa: RowEncoder[A])(f: B ⇒ A) = fa.contramap(f)
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
  implicit val csvErrorEq: Eq[CsvError] = new Eq[CsvError] {
    override def eqv(x: CsvError, y: CsvError): Boolean = x == y
  }
}
