package kantan.csv

import kantan.codecs.{Codec, Decoder, Encoder}

object RowDecoder extends GeneratedRowDecoders {
  def apply[A](f: Seq[String] ⇒ DecodeResult[A]): RowDecoder[A] = Decoder(f)
}

object RowEncoder extends GeneratedRowEncoders {
  def apply[A](f: A ⇒ Seq[String]): RowEncoder[A] = Encoder(f)
}

object RowCodec extends GeneratedRowCodecs {
  def apply[A](decoder: Seq[String] ⇒ DecodeResult[A])(encoder: A ⇒ Seq[String]): RowCodec[A] = Codec(decoder)(encoder)
}