package kantan.csv

import _root_.cats._
import _root_.cats.functor.Contravariant

/** Declares various type class instances for bridging `kantan.csv` and `cats`. */
package object cats extends kantan.codecs.cats.CatsInstances {
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

  /** `Contravariant` instance for `RowEncoder`. */
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
  implicit val csvErrorEq: Eq[CsvError] = new Eq[CsvError] {
    override def eqv(x: CsvError, y: CsvError): Boolean = x == y
  }

  implicit val decodeErrorEq: Eq[DecodeError] = new Eq[DecodeError] {
      override def eqv(x: DecodeError, y: DecodeError): Boolean = x == y
    }
}
