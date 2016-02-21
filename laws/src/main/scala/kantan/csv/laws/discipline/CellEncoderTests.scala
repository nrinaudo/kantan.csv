package kantan.csv.laws.discipline

import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.discipline.EncoderTests
import kantan.csv.CellEncoder
import kantan.csv.laws._
import org.scalacheck.Arbitrary

object CellEncoderTests {
  def apply[A](implicit l: CellEncoderLaws[A], al: Arbitrary[LegalValue[String, A]]): CellEncoderTests[A] =
      EncoderTests[String, A, CellEncoder]
}
