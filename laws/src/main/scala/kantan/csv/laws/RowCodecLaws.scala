package kantan.csv.laws

import kantan.csv.RowCodec

trait RowCodecLaws[A] extends SafeRowCodecLaws[A] with RowDecoderLaws[A]

object RowCodecLaws {
  def apply[A](implicit c: RowCodec[A]): RowCodecLaws[A] = new RowCodecLaws[A] {
    override implicit val codec = c
  }
}