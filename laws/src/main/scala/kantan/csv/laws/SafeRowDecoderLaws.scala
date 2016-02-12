package kantan.csv.laws

import kantan.csv.RowDecoder

trait SafeRowDecoderLaws[A] {
  def rowDecoder: RowDecoder[A]

  def rowDecode(value: ExpectedRow[A]): Boolean =
    rowDecoder.decode(value.encoded).map(_ == value.value).getOrElse(false)

  def rowDecodeIdentity(value: ExpectedRow[A]): Boolean =
    rowDecoder.decode(value.encoded) == rowDecoder.map(identity).decode(value.encoded)

  def rowDecodeComposition[B, C](a: ExpectedRow[A], f: A ⇒ B, g: B ⇒ C): Boolean =
    rowDecoder.map(f andThen g).decode(a.encoded) == rowDecoder.map(f).map(g).decode(a.encoded)
}

object SafeRowDecoderLaws {
  def apply[A](implicit d: RowDecoder[A]): SafeRowDecoderLaws[A] = new SafeRowDecoderLaws[A] {
    override implicit val rowDecoder = d
  }
}