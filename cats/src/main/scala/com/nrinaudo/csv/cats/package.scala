package com.nrinaudo.csv

import _root_.cats.{Eq, Monad}
import _root_.cats.functor.Contravariant
import com.nrinaudo.csv.ops._
import _root_.cats.data.Xor

package object cats {
  // - DecodeResult ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val decodeResultInstances = new Monad[DecodeResult] {
    override def flatMap[A, B](fa: DecodeResult[A])(f: A => DecodeResult[B]) = fa.flatMap(f)
    override def map[A, B](fa: DecodeResult[A])(f: A => B) = fa.map(f)
    override def pure[A](x: A) = DecodeResult(x)
  }

  implicit def decodeResultEq[A: Eq] = new Eq[DecodeResult[A]] {
    override def eqv(x: DecodeResult[A], y: DecodeResult[A]): Boolean = (x, y) match {
      case (DecodeResult.Success(a), DecodeResult.Success(b))                   => Eq[A].eqv(a, b)
      case (DecodeResult.ReadFailure(l1, c1), DecodeResult.ReadFailure(l2, c2)) => l1 == l2 && c1 == c2
      case (DecodeResult.DecodeFailure, DecodeResult.DecodeFailure)             => true
      case _                                                                    => false
    }
  }


  // - CellCodec -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val cellDecoder = new Monad[CellDecoder] {
    override def map[A, B](fa: CellDecoder[A])(f: A => B) = fa.map(f)
    override def flatMap[A, B](fa: CellDecoder[A])(f: A => CellDecoder[B]) = fa.flatMap(f)
    override def pure[A](x: A) = CellDecoder(_ => DecodeResult(x))
  }

  implicit val cellEncoder = new Contravariant[CellEncoder] {
    override def contramap[A, B](fa: CellEncoder[A])(f: B => A) = fa.contramap(f)
  }



  // - RowCodec --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val rowDecoder = new Monad[RowDecoder] {
    override def map[A, B](fa: RowDecoder[A])(f: A => B) = fa.map(f)
    override def flatMap[A, B](fa: RowDecoder[A])(f: A => RowDecoder[B]) = fa.flatMap(f)
    override def pure[A](x: A) = RowDecoder(_ => DecodeResult(x))
  }

  implicit val rowEncoder = new Contravariant[RowEncoder] {
    override def contramap[A, B](fa: RowEncoder[A])(f: B => A) = fa.contramap(f)
  }


  // - CSV input / output ----------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------
  implicit val csvInput: Contravariant[CsvInput] = new Contravariant[CsvInput] {
    override def contramap[A, B](r: CsvInput[A])(f: B => A) = r.contramap(f)
  }

  implicit val csvOutput: Contravariant[CsvOutput] = new Contravariant[CsvOutput] {
    override def contramap[A, B](r: CsvOutput[A])(f: B => A) = r.contramap(f)
  }



  // - Xor -------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def xorCellDecoder[A: CellDecoder, B: CellDecoder]: CellDecoder[Xor[A, B]] =
    CellDecoder { s => CellDecoder[A].decode(s).map(a => Xor.Left(a))
      .orElse(CellDecoder[B].decode(s).map(b => Xor.Right(b)))
    }

  implicit def xorCellEncoder[A: CellEncoder, B: CellEncoder]: CellEncoder[Xor[A, B]] = CellEncoder(eab => eab match {
    case Xor.Left(a)  => a.asCsvCell
    case Xor.Right(b)  => b.asCsvCell
  })

  implicit def xorRowDecoder[A: RowDecoder, B: RowDecoder]: RowDecoder[Xor[A, B]] =
    RowDecoder { s => RowDecoder[A].decode(s).map(a => Xor.Left(a))
      .orElse(RowDecoder[B].decode(s).map(b => Xor.Right(b)))
    }

  implicit def xorRowEncoder[A: RowEncoder, B: RowEncoder]: RowEncoder[Xor[A, B]] = RowEncoder(eab => eab match {
    case Xor.Left(a)  => a.asCsvRow
    case Xor.Right(b)  => b.asCsvRow
  })
}
