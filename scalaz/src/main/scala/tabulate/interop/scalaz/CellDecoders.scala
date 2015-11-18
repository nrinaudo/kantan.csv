package tabulate.interop.scalaz

import export.{export, exports}
import tabulate.{DecodeResult, CellDecoder}

import scalaz.Maybe._
import scalaz.{Maybe, Scalaz, \/}, Scalaz._

@exports
object CellDecoders {
  @export(Instantiated)
  implicit def eitherCellDecoder[A, B](implicit da: CellDecoder[A], db: CellDecoder[B]): CellDecoder[A \/ B] =
    CellDecoder(s => da.decode(s).map(_.left[B]).orElse(db.decode(s).map(_.right[A])))

  @export(Instantiated)
  implicit def maybeDecoder[A](implicit da: CellDecoder[A]): CellDecoder[Maybe[A]] = CellDecoder { s =>
    if(s.isEmpty) DecodeResult.success(empty)
    else          da.decode(s).map(just)
  }
}
