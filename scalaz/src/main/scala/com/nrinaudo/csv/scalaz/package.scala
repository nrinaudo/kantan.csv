package com.nrinaudo.csv

import com.nrinaudo.csv.ops._
import _root_.scalaz.Maybe._
import _root_.scalaz.{Maybe, -\/, \/-, \/}
import _root_.scalaz.syntax.either._
import _root_.scalaz.syntax.maybe._

package object scalaz {
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
