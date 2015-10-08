package com.nrinaudo.csv

import com.nrinaudo.csv.ops._
import _root_.scalaz.{-\/, \/-, \/}
import _root_.scalaz.syntax.either._

package object scalaz {
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
