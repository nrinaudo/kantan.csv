package kantan.csv.scalaz

import export.{export, exports}
import kantan.csv
import kantan.csv.DecodeResult

import scalaz.Maybe._
import scalaz.Scalaz._
import scalaz.{Maybe, \/}

@exports
object RowDecoders {
  @export(Orphan)
  implicit def eitherRowDecoder[A, B](implicit da: csv.RowDecoder[A], db: csv.RowDecoder[B]): csv.RowDecoder[A \/ B] =
    csv.RowDecoder(row ⇒ da.decode(row).map(_.left[B]).orElse(db.decode(row).map(_.right[A])))

  @export(Orphan)
  implicit def maybeDecoder[A](implicit da: csv.RowDecoder[A]): csv.RowDecoder[Maybe[A]] = csv.RowDecoder { row ⇒
    if(row.isEmpty) DecodeResult.success(empty)
    else            da.decode(row).map(just)
  }
}
