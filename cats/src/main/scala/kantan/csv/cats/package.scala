package kantan.csv

import _root_.cats._
import _root_.cats.functor.Contravariant
import kantan.csv

/** Declares various type class instances for bridging `kantan.csv` and `cats`. */
package object cats {
  // - DecodeResult ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Monad` instance for `DecodeResult`. */
  implicit val decodeResultInstances = new Monad[csv.DecodeResult] {
    override def flatMap[A, B](fa: csv.DecodeResult[A])(f: A ⇒ csv.DecodeResult[B]) = fa.flatMap(f)
    override def map[A, B](fa: csv.DecodeResult[A])(f: A ⇒ B) = fa.map(f)
    override def pure[A](x: A) = csv.DecodeResult(x)
  }

  /** `Eq` instance for `DecodeResult`. */
  implicit def decodeResultEq[A: Eq] = new Eq[csv.DecodeResult[A]] {
    override def eqv(x: csv.DecodeResult[A], y: csv.DecodeResult[A]): Boolean = (x, y) match {
      case (csv.DecodeResult.Success(a), csv.DecodeResult.Success(b))                   ⇒ Eq[A].eqv(a, b)
      case (csv.DecodeResult.ReadFailure(l1, c1), csv.DecodeResult.ReadFailure(l2, c2)) ⇒ l1 == l2 && c1 == c2
      case (csv.DecodeResult.DecodeFailure, csv.DecodeResult.DecodeFailure)             ⇒ true
      case _                                                                    ⇒ false
    }
  }


  // - CellCodec -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Monad` instance for `CellDecoder`. */
  implicit val cellDecoder = new Monad[csv.CellDecoder] {
    override def map[A, B](fa: csv.CellDecoder[A])(f: A ⇒ B) = fa.map(f)
    override def flatMap[A, B](fa: csv.CellDecoder[A])(f: A ⇒ csv.CellDecoder[B]) = fa.flatMap(f)
    override def pure[A](x: A) = csv.CellDecoder(_ ⇒ csv.DecodeResult(x))
  }

  /** `Contravariant` instance for `CellEncoder`. */
  implicit val cellEncoder = new Contravariant[csv.CellEncoder] {
    override def contramap[A, B](fa: csv.CellEncoder[A])(f: B ⇒ A) = fa.contramap(f)
  }



  // - RowCodec --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Monad` instance for `RowDecoder`. */
  implicit val rowDecoder = new Monad[csv.RowDecoder] {
    override def map[A, B](fa: csv.RowDecoder[A])(f: A ⇒ B) = fa.map(f)
    override def flatMap[A, B](fa: csv.RowDecoder[A])(f: A ⇒ csv.RowDecoder[B]) = fa.flatMap(f)
    override def pure[A](x: A) = csv.RowDecoder(_ ⇒ csv.DecodeResult(x))
  }

  /** `Contravariant` instance for `RowEncoder`. */
  implicit val rowEncoder = new Contravariant[csv.RowEncoder] {
    override def contramap[A, B](fa: csv.RowEncoder[A])(f: B ⇒ A) = fa.contramap(f)
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
