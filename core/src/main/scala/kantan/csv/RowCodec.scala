package kantan.csv

import kantan.codecs.Codec

/** Provides useful methods for [[RowCodec]] instance creation. */
object RowCodec extends GeneratedRowCodecs {
  /** Creates a new [[RowCodec]] instance from the specified encoding and decoding functions. */
  def apply[A](decoder: Seq[String] ⇒ DecodeResult[A])(encoder: A ⇒ Seq[String]): RowCodec[A] = Codec(decoder)(encoder)
}

trait RowCodecInstances extends RowEncoderInstances with RowDecoderInstances {
  implicit val stringSeqRowCodec: RowCodec[Seq[String]] = RowCodec(ss ⇒ DecodeResult(ss))(identity)
}
