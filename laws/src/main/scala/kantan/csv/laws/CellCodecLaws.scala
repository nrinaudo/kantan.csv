package kantan.csv.laws

import kantan.csv.CellCodec

trait CellCodecLaws[A] extends SafeCellCodecLaws[A] with CellDecoderLaws[A]

object CellCodecLaws {
  def apply[A](implicit c: CellCodec[A]): CellCodecLaws[A] = new CellCodecLaws[A] {
    override implicit val codec = c
  }
}