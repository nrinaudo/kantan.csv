package tabulate.laws

import tabulate._

trait SafeRowCodecLaws[A] extends SafeRowDecoderLaws[A] with RowEncoderLaws[A] {
  def codec: RowCodec[A]
  override def decoder: RowDecoder[A] = codec
  override def encoder: RowEncoder[A] = codec

  def roundTrip(a: A): Boolean = codec.decode(codec.encode(a)) == DecodeResult.Success(a)
}

object SafeRowCodecLaws {
  def apply[A](implicit c: RowCodec[A]): SafeRowCodecLaws[A] = new SafeRowCodecLaws[A] {
    override implicit val codec = c
  }
}