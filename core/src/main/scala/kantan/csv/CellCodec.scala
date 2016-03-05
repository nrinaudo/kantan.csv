package kantan.csv

import kantan.codecs.Codec

/** Declares helpful methods for [[CellCodec]] creation. */
object CellCodec {
  /** Creates a new [[CellCodec]] from the specified decoding and encoding functions. */
  def apply[A](decoder: String ⇒ DecodeResult[A])(encoder: A ⇒ String): CellCodec[A] = Codec(decoder)(encoder)
}

trait CellCodecInstances extends CellEncoderInstances with CellDecoderInstances

