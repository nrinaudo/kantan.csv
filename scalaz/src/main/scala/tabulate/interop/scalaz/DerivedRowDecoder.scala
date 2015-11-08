package tabulate.interop.scalaz

import export.exports
import tabulate.RowDecoder

import scalaz.\/
import scalaz.Scalaz._

trait DerivedRowDecoder[A] extends RowDecoder[A]

@exports
object DerivedRowDecoder {
  implicit def eitherRowDecoder[A: RowDecoder, B: RowDecoder]: RowDecoder[A \/ B] =
    RowDecoder { s => RowDecoder[A].decode(s).map(_.left[B])
      .orElse(RowDecoder[B].decode(s).map(_.right[A]))
    }
}
