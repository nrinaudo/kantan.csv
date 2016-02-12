package kantan.csv.laws

import kantan.csv.CellDecoder
import org.scalacheck.Prop._

trait CellDecoderLaws[A] extends SafeCellDecoderLaws[A] with RowDecoderLaws[A] {
  def safeCellDecodeFail(cell: IllegalCell[A]): Boolean = cellDecoder.decode(cell.value).isFailure

  def unsafeCellDecodeFail(cell: IllegalCell[A]): Boolean =
    throws(classOf[java.lang.Exception])(cellDecoder.unsafeDecode(cell.value))
}

object CellDecoderLaws {
  def apply[A](implicit c: CellDecoder[A]): CellDecoderLaws[A] = new CellDecoderLaws[A] {
    override implicit val cellDecoder = c
  }
}