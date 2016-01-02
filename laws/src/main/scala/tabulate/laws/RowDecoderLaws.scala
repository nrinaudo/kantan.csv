package tabulate.laws

import org.scalacheck.Prop._
import tabulate.RowDecoder

trait RowDecoderLaws[A] extends SafeRowDecoderLaws[A] {
  def safeDecodeFail(row: IllegalRow[A]): Boolean = decoder.decode(row.value).isFailure

  def unsafeDecodeFail(row: IllegalRow[A]): Boolean =
    throws(classOf[java.lang.Exception])(decoder.unsafeDecode(row.value))
}

object RowDecoderLaws {
  def apply[A](implicit c: RowDecoder[A]): RowDecoderLaws[A] = new RowDecoderLaws[A] {
    override implicit val decoder = c
  }
}
