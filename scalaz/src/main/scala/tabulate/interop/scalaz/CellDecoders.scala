package tabulate.interop.scalaz

import export.{export, exports}
import tabulate.{DecodeResult, CellDecoder}

import scalaz.Maybe._
import scalaz.{Maybe, Scalaz, \/}, Scalaz._

@exports
object CellDecoders {
  @export(Instantiated)
  implicit def eitherCellDecoder[A: CellDecoder, B: CellDecoder]: CellDecoder[A \/ B] =
CellDecoder(s => CellDecoder[A].decode(s).map(_.left[B])
      .orElse(CellDecoder[B].decode(s).map(_.right[A])))

  @export(Instantiated)
  implicit def maybeDecoder[A: CellDecoder]: CellDecoder[Maybe[A]] = CellDecoder { s =>
    if(s.isEmpty) DecodeResult.success(empty)
    else          CellDecoder[A].decode(s).map(just)
  }
}
