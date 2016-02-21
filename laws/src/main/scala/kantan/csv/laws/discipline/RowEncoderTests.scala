package kantan.csv.laws.discipline

import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.discipline.EncoderTests
import kantan.csv.RowEncoder
import kantan.csv.laws._
import org.scalacheck.Arbitrary

object RowEncoderTests {
  def apply[A](implicit l: RowEncoderLaws[A], al: Arbitrary[LegalValue[Seq[String], A]]): RowEncoderTests[A] =
    EncoderTests[Seq[String], A, RowEncoder]
}
