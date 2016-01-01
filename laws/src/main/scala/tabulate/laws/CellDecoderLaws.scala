package tabulate.laws

import org.scalacheck.Prop._

trait CellDecoderLaws[A] extends SafeCellDecoderLaws[A] {
  def safeDecodeFail(cell: IllegalValue[A]): Boolean = decoder.decode(cell.value).isFailure

  def unsafeDecodeFail(cell: IllegalValue[A]): Boolean =
    throws(classOf[java.lang.Exception])(decoder.unsafeDecode(cell.value))
}
