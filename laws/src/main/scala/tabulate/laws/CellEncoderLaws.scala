package tabulate.laws

import tabulate.{RowEncoder, CellEncoder}

trait CellEncoderLaws[A] extends RowEncoderLaws[A] {
  def cellEncoder: CellEncoder[A]
  override def rowEncoder = RowEncoder.cellEncoder(cellEncoder)

  def cellEncode(value: ExpectedCell[A]): Boolean = cellEncoder.encode(value.value) == value.encoded

  def cellEncodeIdentity(a: A): Boolean =
    cellEncoder.encode(a) == cellEncoder.contramap[A](identity).encode(a)

  def cellEncodeComposition[B, C](c: C, f: B ⇒ A, g: C ⇒ B): Boolean =
    cellEncoder.contramap(g andThen f).encode(c) == cellEncoder.contramap(f).contramap(g).encode(c)
}

object CellEncoderLaws {
  def apply[A](implicit c: CellEncoder[A]): CellEncoderLaws[A] = new CellEncoderLaws[A] {
    override implicit val cellEncoder = c
  }
}