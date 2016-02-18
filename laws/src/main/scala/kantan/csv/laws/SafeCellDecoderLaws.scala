package kantan.csv.laws

import kantan.csv
import kantan.csv._
import org.scalacheck.Prop._

trait SafeCellDecoderLaws[A] extends RowDecoderLaws[A] {
  def cellDecoder: CellDecoder[A]
  override def rowDecoder = csv.RowDecoder.cellDecoder(cellDecoder)

  def safeOutOfBounds(row: List[String]): Boolean = cellDecoder.decode(row, row.length).isFailure

  def unsafeOutputOfBounds(row: List[String]): Boolean =
    throws(classOf[IndexOutOfBoundsException])(cellDecoder.unsafeDecode(row, row.length))

  def cellDecode(value: ExpectedCell[A]): Boolean = cellDecoder.decode(value.encoded) == CsvResult(value.value)

  def unsafeCellDecode(value: ExpectedCell[A]): Boolean = cellDecoder.decode(value.encoded) == value.value

  def cellDecodeIdentity(value: ExpectedCell[A]): Boolean =
    cellDecoder.decode(value.encoded) == cellDecoder.map(identity).decode(value.encoded)

  def cellDecodeComposition[B, C](value: ExpectedCell[A], f: A ⇒ B, g: B ⇒ C): Boolean =
    cellDecoder.map(f andThen g).decode(value.encoded) == cellDecoder.map(f).map(g).decode(value.encoded)
}

object SafeCellDecoderLaws {
  def apply[A](implicit d: CellDecoder[A]): SafeCellDecoderLaws[A] = new SafeCellDecoderLaws[A] {
    override implicit val cellDecoder = d
  }
}