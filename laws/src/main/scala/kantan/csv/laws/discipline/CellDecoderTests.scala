package kantan.csv.laws.discipline

import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.discipline.DecoderTests
import kantan.csv.laws._
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.{codecs, DecodeError}
import org.scalacheck.Arbitrary

object CellDecoderTests {
  def apply[A](implicit l: CellDecoderLaws[A], al: Arbitrary[LegalValue[String, A]]): CellDecoderTests[A] =
    DecoderTests[String, A, DecodeError, codecs.type]
}
