package kantan.csv

import kantan.codecs.Codec

object RowCodec extends GeneratedRowCodecs {
  def apply[A](decoder: Seq[String] ⇒ DecodeResult[A])(encoder: A ⇒ Seq[String]): RowCodec[A] = Codec(decoder)(encoder)
}

trait RowCodecInstances extends RowEncoderInstances with RowDecoderInstances {
  implicit val stringSeqRowCodec: RowCodec[Seq[String]] = RowCodec(ss ⇒ DecodeResult(ss))(identity)
}
