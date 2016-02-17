package kantan.csv

import kantan.csv

import _root_.scalaz._

/** Declares various type class instances for bridging `kantan.csv` and `scalaz`. */
package object scalaz {
  // - DecodeResult ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Monad` instance for `DecodeResult`. */
  implicit val decodeResultInstances: Monad[DecodeResult] = new Monad[DecodeResult] {
    override def bind[A, B](fa: DecodeResult[A])(f: A ⇒ DecodeResult[B]) = fa.flatMap(f)
    override def map[A, B](fa: DecodeResult[A])(f: A ⇒ B) = fa.map(f)
    override def point[A](x: ⇒ A) = DecodeResult(x)
  }

  /** `Equal` instance for `DecodeResult`. */
  implicit def decodeResultEqual[A: Equal]: Equal[DecodeResult[A]] = new Equal[DecodeResult[A]] {
    override def equal(a1: DecodeResult[A], a2: DecodeResult[A]) = (a1, a2) match {
      case (DecodeResult.Success(a), DecodeResult.Success(b))                   ⇒ Equal[A].equal(a, b)
      case (DecodeResult.ReadFailure(l1, c1), DecodeResult.ReadFailure(l2, c2)) ⇒ l1 == l2 && c1 == c2
      case (DecodeResult.DecodeFailure, DecodeResult.DecodeFailure)             ⇒ true
      case _                                                                    ⇒ false
    }
  }


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
}
