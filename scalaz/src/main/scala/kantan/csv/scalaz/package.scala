package kantan.csv

import kantan.csv

import _root_.scalaz._

/** Declares various type class instances for bridging `kantan.csv` and `scalaz`. */
package object scalaz {
  // - DecodeResult ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Monad` instance for `DecodeResult`. */
  implicit val decodeResultInstances: Monad[csv.DecodeResult] = new Monad[csv.DecodeResult] {
    override def bind[A, B](fa: csv.DecodeResult[A])(f: A ⇒ csv.DecodeResult[B]) = fa.flatMap(f)
    override def map[A, B](fa: csv.DecodeResult[A])(f: A ⇒ B) = fa.map(f)
    override def point[A](x: ⇒ A) = csv.DecodeResult(x)
  }

  /** `Equal` instance for `DecodeResult`. */
  implicit def decodeResultEqual[A: Equal]: Equal[csv.DecodeResult[A]] = new Equal[csv.DecodeResult[A]] {
    override def equal(a1: csv.DecodeResult[A], a2: csv.DecodeResult[A]) = (a1, a2) match {
      case (csv.DecodeResult.Success(a), csv.DecodeResult.Success(b))                   ⇒ Equal[A].equal(a, b)
      case (csv.DecodeResult.ReadFailure(l1, c1), csv.DecodeResult.ReadFailure(l2, c2)) ⇒ l1 == l2 && c1 == c2
      case (csv.DecodeResult.DecodeFailure, csv.DecodeResult.DecodeFailure)             ⇒ true
      case _                                                                    ⇒ false
    }
  }


  // - CellCodec -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Monad` instance for `CellDecoder`. */
  implicit val cellDecoder: Monad[csv.CellDecoder] = new Monad[csv.CellDecoder] {
    override def map[A, B](fa: csv.CellDecoder[A])(f: A ⇒ B) = fa.map(f)
    override def point[A](a: ⇒ A) = csv.CellDecoder(_ ⇒ csv.DecodeResult(a))
    override def bind[A, B](fa: csv.CellDecoder[A])(f: A ⇒ csv.CellDecoder[B]) = fa.flatMap(f)
  }

  /** `Contravariant` instance for `CellEncoder`. */
  implicit val cellEncoder: Contravariant[csv.CellEncoder] = new Contravariant[csv.CellEncoder] {
    override def contramap[A, B](r: csv.CellEncoder[A])(f: B ⇒ A) = r.contramap(f)
  }



  // - RowCodec --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Monad` instance for `RowDecoder`. */
  implicit val rowDecoder: Monad[csv.RowDecoder] = new Monad[csv.RowDecoder] {
    override def map[A, B](fa: csv.RowDecoder[A])(f: A ⇒ B) = fa.map(f)
    override def point[A](a: ⇒ A) = csv.RowDecoder(_ ⇒ csv.DecodeResult(a))
    override def bind[A, B](fa: csv.RowDecoder[A])(f: A ⇒ csv.RowDecoder[B]) = fa.flatMap(f)
  }

  /** `Contravariant` instance for `CellEncoder`. */
  implicit val rowEncoder: Contravariant[csv.RowEncoder] = new Contravariant[csv.RowEncoder] {
    override def contramap[A, B](r: csv.RowEncoder[A])(f: B ⇒ A) = r.contramap(f)
  }



  // - CSV input / output ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Contravariant` instance for `CsvInput`. */
  implicit val csvInput: Contravariant[csv.CsvInput] = new Contravariant[csv.CsvInput] {
    override def contramap[A, B](r: csv.CsvInput[A])(f: B ⇒ A) = r.contramap(f)
  }

  /** `Contravariant` instance for `CsvOutput`. */
  implicit val csvOutput: Contravariant[csv.CsvOutput] = new Contravariant[csv.CsvOutput] {
    override def contramap[A, B](r: csv.CsvOutput[A])(f: B ⇒ A) = r.contramap(f)
  }
}
