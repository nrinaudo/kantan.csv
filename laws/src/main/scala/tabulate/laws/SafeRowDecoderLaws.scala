package tabulate.laws

import tabulate.{DecodeResult, RowDecoder}

trait SafeRowDecoderLaws[A] {
  def decoder: RowDecoder[A]

  def decode(value: ExpectedRow[A]): Boolean =
    decoder.decode(value.encoded).map(_ == value.value).getOrElse(false)

  def decodeIdentity(value: ExpectedRow[A]): Boolean =
    decoder.decode(value.encoded) == decoder.map(identity).decode(value.encoded)

  def decodeComposition[B, C](a: ExpectedRow[A], f: A => B, g: B => C): Boolean =
    decoder.map(f andThen g).decode(a.encoded) == decoder.map(f).map(g).decode(a.encoded)
}

object SafeRowDecoderLaws {
  def apply[A](implicit d: RowDecoder[A]): SafeRowDecoderLaws[A] = new SafeRowDecoderLaws[A] {
    override implicit val decoder = d
  }
}