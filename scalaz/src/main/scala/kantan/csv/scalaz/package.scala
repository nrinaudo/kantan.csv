package kantan.csv

import _root_.scalaz._

/** Declares various type class instances for bridging `kantan.csv` and `scalaz`. */
package object scalaz extends kantan.codecs.scalaz.ScalazInstances {
  // - CellCodec -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Functor` instance for `CellDecoder`. */
  implicit val cellDecoder: Functor[CellDecoder] = new Functor[CellDecoder] {
    override def map[A, B](fa: CellDecoder[A])(f: A ⇒ B) = fa.map(f)
  }

  /** `Contravariant` instance for `CellEncoder`. */
  implicit val cellEncoder: Contravariant[CellEncoder] = new Contravariant[CellEncoder] {
    override def contramap[A, B](r: CellEncoder[A])(f: B ⇒ A) = r.contramap(f)
  }



  // - RowCodec --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Functor` instance for `RowDecoder`. */
  implicit val rowDecoder: Functor[RowDecoder] = new Functor[RowDecoder] {
    override def map[A, B](fa: RowDecoder[A])(f: A ⇒ B) = fa.map(f)
  }

  /** `Contravariant` instance for `CellEncoder`. */
  implicit val rowEncoder: Contravariant[RowEncoder] = new Contravariant[RowEncoder] {
    override def contramap[A, B](r: RowEncoder[A])(f: B ⇒ A) = r.contramap(f)
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
}
