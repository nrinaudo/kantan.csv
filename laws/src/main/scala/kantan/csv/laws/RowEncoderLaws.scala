package kantan.csv.laws

import kantan.csv.RowEncoder

trait RowEncoderLaws[A] {
  def rowEncoder: RowEncoder[A]

  def rowEncode(value: ExpectedRow[A]): Boolean = rowEncoder.encode(value.value) == value.encoded

  def rowEncodeIdentity(a: A): Boolean =
    rowEncoder.encode(a) == rowEncoder.contramap[A](identity).encode(a)

  def rowEncodeComposition[B, C](c: C, f: B ⇒ A, g: C ⇒ B): Boolean =
    rowEncoder.contramap(g andThen f).encode(c) == rowEncoder.contramap(f).contramap(g).encode(c)
}

object RowEncoderLaws {
  def apply[A](implicit c: RowEncoder[A]): RowEncoderLaws[A] = new RowEncoderLaws[A] {
    override implicit val rowEncoder = c
  }
}