package tabulate.interop.scalaz

import export.{export, exports}
import tabulate.RowDecoder

import scalaz.Scalaz._
import scalaz.\/

@exports
object RowDecoders {
  @export(Instantiated)
  implicit def eitherRowDecoder[A: RowDecoder, B: RowDecoder]: RowDecoder[A \/ B] =
    RowDecoder(row => RowDecoder[A].decode(row).map(_.left[B])
      .orElse(RowDecoder[B].decode(row).map(_.right[A])))
}
