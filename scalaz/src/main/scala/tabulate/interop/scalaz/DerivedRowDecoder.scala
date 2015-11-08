package tabulate.interop.scalaz

import export.exports
import tabulate.RowDecoder

import scalaz.Scalaz._
import scalaz.\/

trait DerivedRowDecoder[A] extends RowDecoder[A]

@exports
object DerivedRowDecoder {
  implicit def eitherRowDecoder[A: RowDecoder, B: RowDecoder]: DerivedRowDecoder[A \/ B] =
  new DerivedRowDecoder[\/[A, B]] {
    override def decode(row: Seq[String]) = RowDecoder[A].decode(row).map(_.left[B])
      .orElse(RowDecoder[B].decode(row).map(_.right[A]))
  }
}
