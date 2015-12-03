package tabulate.interop.scalaz

import export.{export, exports}
import tabulate.{DecodeResult, RowDecoder}

import scalaz.Maybe._
import scalaz.Scalaz._
import scalaz.{Maybe, \/}

@exports
object RowDecoders {
  @export(Orphan)
  implicit def eitherRowDecoder[A, B](implicit da: RowDecoder[A], db: RowDecoder[B]): RowDecoder[A \/ B] =
    RowDecoder(row => da.decode(row).map(_.left[B]).orElse(db.decode(row).map(_.right[A])))

  @export(Orphan)
  implicit def maybeDecoder[A](implicit da: RowDecoder[A]): RowDecoder[Maybe[A]] = RowDecoder { row =>
    if(row.isEmpty) DecodeResult.success(empty)
    else            da.decode(row).map(just)
  }
}
