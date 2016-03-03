package kantan.csv

import kantan.codecs.{Encoder, Decoder, Codec}

object CellEncoder {
  /** Creates a new [[kantan.csv.CellEncoder]] from the specified function. */
  def apply[A](f: A ⇒ String): CellEncoder[A] = Encoder(f)
}

object CellDecoder {
  /** Creates a new instance of [[CellDecoder]] that uses the specified function to parse data. */
  def apply[A](f: String ⇒ DecodeResult[A]): CellDecoder[A] = Decoder(f)
}

/** Declares helpful methods for [[CellCodec]] creation. */
object CellCodec {
  /** Creates a new [[CellCodec]] from the specified decoding and encoding functions. */
  def apply[A](decoder: String ⇒ DecodeResult[A])(encoder: A ⇒ String): CellCodec[A] = Codec(decoder)(encoder)
}