package com.nrinaudo.csv

import com.nrinaudo.csv.ops._
import _root_.scalaz.Maybe._
import _root_.scalaz._
import _root_.scalaz.syntax.either._
import _root_.scalaz.syntax.maybe._

package object scalaz {
  // - CellCodec -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val cellDecoder: Monad[CellDecoder] = new Monad[CellDecoder] {
    override def map[A, B](fa: CellDecoder[A])(f: A => B) = fa.map(f)
    override def point[A](a: => A) = CellDecoder(_ => DecodeResult(a))
    override def bind[A, B](fa: CellDecoder[A])(f: A => CellDecoder[B]) = fa.flatMap(f)
  }

  implicit val cellEncoder: Contravariant[CellEncoder] = new Contravariant[CellEncoder] {
    override def contramap[A, B](r: CellEncoder[A])(f: B => A) = r.contramap(f)
  }



  // - RowCodec --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val rowDecoder: Monad[RowDecoder] = new Monad[RowDecoder] {
    override def map[A, B](fa: RowDecoder[A])(f: A => B) = fa.map(f)
    override def point[A](a: => A) = RowDecoder(_ => DecodeResult(a))
    override def bind[A, B](fa: RowDecoder[A])(f: A => RowDecoder[B]) = fa.flatMap(f)
  }

  implicit val rowEncoder: Contravariant[RowEncoder] = new Contravariant[RowEncoder] {
    override def contramap[A, B](r: RowEncoder[A])(f: B => A) = r.contramap(f)
  }



  // - CSV input / output ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val csvInput: Contravariant[CsvInput] = new Contravariant[CsvInput] {
    override def contramap[A, B](r: CsvInput[A])(f: B => A) = r.contramap(f)
  }

  implicit val csvOutput: Contravariant[CsvOutput] = new Contravariant[CsvOutput] {
    override def contramap[A, B](r: CsvOutput[A])(f: B => A) = r.contramap(f)
  }


  // - Maybe -----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def maybeDecoder[A: CellDecoder]: CellDecoder[Maybe[A]] = CellDecoder { s =>
    if(s.isEmpty) DecodeResult.success(empty)
    else          CellDecoder[A].decode(s).map(just)
  }

  implicit def maybeEncoder[A: CellEncoder]: CellEncoder[Maybe[A]] =
    CellEncoder(ma => ma.map(CellEncoder[A].encode).getOrElse(""))



  // - \/ --------------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def eitherCellDecoder[A: CellDecoder, B: CellDecoder]: CellDecoder[A \/ B] =
    CellDecoder { s => CellDecoder[A].decode(s).map(_.left[B])
      .orElse(CellDecoder[B].decode(s).map(_.right[A]))
    }

  implicit def eitherCellEncoder[A: CellEncoder, B: CellEncoder]: CellEncoder[A \/ B] = CellEncoder(eab => eab match {
    case -\/(a)  => a.asCsvCell
    case \/-(b)  => b.asCsvCell
  })

  implicit def eitherRowDecoder[A: RowDecoder, B: RowDecoder]: RowDecoder[A \/ B] =
    RowDecoder { s => RowDecoder[A].decode(s).map(_.left[B])
      .orElse(RowDecoder[B].decode(s).map(_.right[A]))
    }

  implicit def eitherRowEncoder[A: RowEncoder, B: RowEncoder]: RowEncoder[A \/ B] = RowEncoder(eab => eab match {
    case -\/(a)  => a.asCsvRow
    case \/-(b)  => b.asCsvRow
  })
}
