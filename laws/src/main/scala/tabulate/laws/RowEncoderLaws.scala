package tabulate.laws

import tabulate.{CellEncoder, RowEncoder}

trait RowEncoderLaws[A] {
  def encoder: RowEncoder[A]

  def encode(value: ExpectedRow[A]): Boolean = encoder.encode(value.value) == value.encoded

  def encodeIdentity(a: A): Boolean =
    encoder.encode(a) == encoder.contramap[A](identity).encode(a)

  def encodeComposition[B, C](c: C, f: B => A, g: C => B): Boolean =
    encoder.contramap(g andThen f).encode(c) == encoder.contramap(f).contramap(g).encode(c)
}

object RowEncoderLaws {
  def apply[A](implicit c: RowEncoder[A]): RowEncoderLaws[A] = new RowEncoderLaws[A] {
    override implicit val encoder = c
  }
}