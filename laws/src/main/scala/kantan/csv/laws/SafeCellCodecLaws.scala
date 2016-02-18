package kantan.csv.laws

import kantan.csv.{CellCodec, CellDecoder, CellEncoder, CsvResult}

trait SafeCellCodecLaws[A] extends SafeCellDecoderLaws[A] with CellEncoderLaws[A] {
  def codec: CellCodec[A]
  override def cellDecoder: CellDecoder[A] = codec
  override def cellEncoder: CellEncoder[A] = codec

  def roundTrip(a: A): Boolean = codec.decode(codec.encode(a)) == CsvResult(a)
}

object SafeCellCodecLaws {
  def apply[A](implicit c: CellCodec[A]): SafeCellCodecLaws[A] = new SafeCellCodecLaws[A] {
    override implicit val codec = c
  }
}