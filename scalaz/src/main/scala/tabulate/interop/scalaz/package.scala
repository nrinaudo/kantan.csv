package tabulate.interop

import tabulate._
import ops._
import _root_.scalaz.Maybe._
import _root_.scalaz._
import _root_.scalaz.Scalaz._


/** Declares various type class instances for bridging `tabulate` and `scalaz`. */
package object scalaz {
  // - DecodeResult ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Monad` instance for [[DecodeResult]]. */
  implicit val decodeResultInstances: Monad[DecodeResult] = new Monad[DecodeResult] {
    override def bind[A, B](fa: DecodeResult[A])(f: A => DecodeResult[B]) = fa.flatMap(f)
    override def map[A, B](fa: DecodeResult[A])(f: A => B) = fa.map(f)
    override def point[A](x: => A) = DecodeResult(x)
  }

  /** `Equal` instance for [[DecodeResult]]. */
  implicit def decodeResultEqual[A: Equal]: Equal[DecodeResult[A]] = new Equal[DecodeResult[A]] {
    override def equal(a1: DecodeResult[A], a2: DecodeResult[A]) = (a1, a2) match {
      case (DecodeResult.Success(a), DecodeResult.Success(b))                   => Equal[A].equal(a, b)
      case (DecodeResult.ReadFailure(l1, c1), DecodeResult.ReadFailure(l2, c2)) => l1 == l2 && c1 == c2
      case (DecodeResult.DecodeFailure, DecodeResult.DecodeFailure)             => true
      case _                                                                    => false
    }
  }


  // - CellCodec -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Monad` instance for [[CellDecoder]]. */
  implicit val cellDecoder: Monad[CellDecoder] = new Monad[CellDecoder] {
    override def map[A, B](fa: CellDecoder[A])(f: A => B) = fa.map(f)
    override def point[A](a: => A) = CellDecoder(_ => DecodeResult(a))
    override def bind[A, B](fa: CellDecoder[A])(f: A => CellDecoder[B]) = fa.flatMap(f)
  }

  /** `Contravariant` instance for [[CellEncoder]]. */
  implicit val cellEncoder: Contravariant[CellEncoder] = new Contravariant[CellEncoder] {
    override def contramap[A, B](r: CellEncoder[A])(f: B => A) = r.contramap(f)
  }



  // - RowCodec --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Monad` instance for [[RowDecoder]]. */
  implicit val rowDecoder: Monad[RowDecoder] = new Monad[RowDecoder] {
    override def map[A, B](fa: RowDecoder[A])(f: A => B) = fa.map(f)
    override def point[A](a: => A) = RowDecoder(_ => DecodeResult(a))
    override def bind[A, B](fa: RowDecoder[A])(f: A => RowDecoder[B]) = fa.flatMap(f)
  }

  /** `Contravariant` instance for [[CellEncoder]]. */
  implicit val rowEncoder: Contravariant[RowEncoder] = new Contravariant[RowEncoder] {
    override def contramap[A, B](r: RowEncoder[A])(f: B => A) = r.contramap(f)
  }



  // - CSV input / output ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Contravariant` instance for [[CsvInput]]. */
  implicit val csvInput: Contravariant[CsvInput] = new Contravariant[CsvInput] {
    override def contramap[A, B](r: CsvInput[A])(f: B => A) = r.contramap(f)
  }

  /** `Contravariant` instance for [[CsvOutput]]. */
  implicit val csvOutput: Contravariant[CsvOutput] = new Contravariant[CsvOutput] {
    override def contramap[A, B](r: CsvOutput[A])(f: B => A) = r.contramap(f)
  }


  // - Maybe -----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** [[CellDecoder]] instance for `Maybe`. */
  implicit def maybeDecoder[A: CellDecoder]: CellDecoder[Maybe[A]] = CellDecoder { s =>
    if(s.isEmpty) DecodeResult.success(empty)
    else          CellDecoder[A].decode(s).map(just)
  }

  /** [[CellEncoder]] instance for `Maybe`. */
  implicit def maybeEncoder[A: CellEncoder]: CellEncoder[Maybe[A]] =
    CellEncoder(ma => ma.map(CellEncoder[A].encode).getOrElse(""))



  // - \/ --------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** [[CellDecoder]] instance for `\/`. */
  implicit def eitherCellDecoder[A: CellDecoder, B: CellDecoder]: CellDecoder[A \/ B] =
    CellDecoder { s => CellDecoder[A].decode(s).map(_.left[B])
      .orElse(CellDecoder[B].decode(s).map(_.right[A]))
    }

  /** [[CellEncoder]] instance for `\/`. */
  implicit def eitherCellEncoder[A: CellEncoder, B: CellEncoder]: CellEncoder[A \/ B] = CellEncoder(eab => eab match {
    case -\/(a)  => a.asCsvCell
    case \/-(b)  => b.asCsvCell
  })

  /** [[RowDecoder]] instance for `\/`. */
  implicit def eitherRowDecoder[A: RowDecoder, B: RowDecoder]: RowDecoder[A \/ B] =
    RowDecoder { s => RowDecoder[A].decode(s).map(_.left[B])
      .orElse(RowDecoder[B].decode(s).map(_.right[A]))
    }

  /** [[RowEncoder]] instance for `\/`. */
  implicit def eitherRowEncoder[A: RowEncoder, B: RowEncoder]: RowEncoder[A \/ B] = RowEncoder(eab => eab match {
    case -\/(a)  => a.asCsvRow
    case \/-(b)  => b.asCsvRow
  })


  // - Misc. -----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def foldableRowEncoder[A: CellEncoder, F[_]: Foldable]: RowEncoder[F[A]] = new RowEncoder[F[A]] {
    override def encode(as: F[A]): Seq[String] = Foldable[F].foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += a.asCsvCell).result()
  }
}
