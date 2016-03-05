package kantan.csv

import kantan.codecs.Decoder
import kantan.codecs.strings._
import kantan.csv.DecodeError.TypeError

object CellDecoder {
  /** Creates a new instance of [[CellDecoder]] that uses the specified function to parse data. */
  def apply[A](f: String ⇒ DecodeResult[A]): CellDecoder[A] = Decoder(f)
}

trait CellDecoderInstances {
  implicit def fromStringDecoder[A](implicit da: StringDecoder[A]): CellDecoder[A] =
    da.tag[codecs.type].mapError(e ⇒ TypeError(e))
}
