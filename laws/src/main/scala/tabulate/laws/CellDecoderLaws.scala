package tabulate.laws

import org.scalacheck.Prop._
import tabulate.CellDecoder

trait CellDecoderLaws[A] extends SafeCellDecoderLaws[A] {
  def safeDecodeFail(cell: IllegalCell[A]): Boolean = decoder.decode(cell.value).isFailure

  def unsafeDecodeFail(cell: IllegalCell[A]): Boolean =
    throws(classOf[java.lang.Exception])(decoder.unsafeDecode(cell.value))
}

object CellDecoderLaws {
  def apply[A](implicit c: CellDecoder[A]): CellDecoderLaws[A] = new CellDecoderLaws[A] {
    override implicit val decoder = c
  }
}