package tabulate.laws

import org.scalacheck.Prop._
import tabulate.CellDecoder

trait CellDecoderLaws[A] extends SafeCellDecoderLaws[A] {
  def safeDecodeFail(cell: IllegalValue[A]): Boolean = {
    if(!decoder.decode(cell.value).isFailure) println(cell)
    decoder.decode(cell.value).isFailure
  }

  def unsafeDecodeFail(cell: IllegalValue[A]): Boolean =
    throws(classOf[java.lang.Exception])(decoder.unsafeDecode(cell.value))
}

object CellDecoderLaws {
  def apply[A](implicit c: CellDecoder[A]): CellDecoderLaws[A] = new CellDecoderLaws[A] {
    override implicit val decoder = c
  }
}