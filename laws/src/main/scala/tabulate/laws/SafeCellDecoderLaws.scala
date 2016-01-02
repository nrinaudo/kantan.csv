package tabulate.laws

import org.scalacheck.Prop._
import tabulate.{CellDecoder, DecodeResult}

trait SafeCellDecoderLaws[A] {
  def decoder: CellDecoder[A]

  def safeOutOfBounds(row: List[String]): Boolean = decoder.decode(row, row.length).isFailure

  def unsafeOutputOfBounds(row: List[String]): Boolean =
    throws(classOf[IndexOutOfBoundsException])(decoder.unsafeDecode(row, row.length))

  def decode(value: ExpectedCell[A]): Boolean = decoder.decode(value.encoded) == DecodeResult.success(value.value)

  def unsafeDecode(value: ExpectedCell[A]): Boolean = decoder.decode(value.encoded) == value.value

  def decodeIdentity(value: ExpectedCell[A]): Boolean =
    decoder.decode(value.encoded) == decoder.map(identity).decode(value.encoded)

  def decodeComposition[B, C](value: ExpectedCell[A], f: A => B, g: B => C): Boolean =
      decoder.map(f andThen g).decode(value.encoded) == decoder.map(f).map(g).decode(value.encoded)
}

object SafeCellDecoderLaws {
  def apply[A](implicit d: CellDecoder[A]): SafeCellDecoderLaws[A] = new SafeCellDecoderLaws[A] {
    override implicit val decoder = d
  }
}