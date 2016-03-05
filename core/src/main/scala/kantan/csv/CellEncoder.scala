package kantan.csv

import kantan.codecs.Encoder
import kantan.codecs.strings._
import kantan.csv.DecodeError.TypeError

object CellEncoder {
  /** Creates a new [[kantan.csv.CellEncoder]] from the specified function. */
  def apply[A](f: A ⇒ String): CellEncoder[A] = Encoder(f)
}

trait CellEncoderInstances {
  implicit def fromStringEncoder[A](implicit ea: StringEncoder[A]): CellEncoder[A] = ea.tag[codecs.type]
}