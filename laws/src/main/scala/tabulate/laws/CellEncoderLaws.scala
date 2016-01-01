package tabulate.laws

import tabulate.CellEncoder

trait CellEncoderLaws[A] {
  def encoder: CellEncoder[A]

  def encode(value: ExpectedValue[A]): Boolean = encoder.encode(value.value) == value.encoded

  def encodeIdentity(a: A): Boolean =
    encoder.encode(a) == encoder.contramap[A](identity).encode(a)

  def encodeComposition[B, C](c: C, f: B => A, g: C => B): Boolean =
    encoder.contramap(g andThen f).encode(c) == encoder.contramap(f).contramap(g).encode(c)
}
