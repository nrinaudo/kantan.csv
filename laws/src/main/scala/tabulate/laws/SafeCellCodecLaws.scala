package tabulate.laws

import tabulate.{CellDecoder, CellEncoder, CellCodec, DecodeResult}

trait SafeCellCodecLaws[A] extends SafeCellDecoderLaws[A] with CellEncoderLaws[A] {
  def codec: CellCodec[A]
  override def cellDecoder: CellDecoder[A] = codec
  override def cellEncoder: CellEncoder[A] = codec

  def roundTrip(a: A): Boolean = codec.decode(codec.encode(a)) == DecodeResult.Success(a)
}

object SafeCellCodecLaws {
  def apply[A](implicit c: CellCodec[A]): SafeCellCodecLaws[A] = new SafeCellCodecLaws[A] {
    override implicit val codec = c
  }
}