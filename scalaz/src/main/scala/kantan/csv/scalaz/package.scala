package kantan.csv

import _root_.scalaz._

/** Declares various type class instances for bridging `kantan.csv` and `scalaz`. */
package object scalaz extends kantan.codecs.scalaz.ScalazInstances {
  // - CellCodec -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Functor` instance for `CellDecoder`. */
  implicit val cellDecoder = decoderFunctor[String, DecodeError, CellDecoder]

  /** `Contravariant` instance for `CellEncoder`. */
  implicit val cellEncoder = encoderContravariant[String, CellEncoder]



  // - RowCodec --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Functor` instance for `RowDecoder`. */
  implicit val rowDecoder = decoderFunctor[Seq[String], DecodeError, RowDecoder]

  /** `Contravariant` instance for `CellEncoder`. */
  implicit val rowEncoder = encoderContravariant[Seq[String], RowEncoder]



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
