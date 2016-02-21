package kantan.csv.laws.discipline

import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.discipline.CodecTests
import kantan.csv._
import kantan.csv.laws._
import kantan.csv.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary

object CellCodecTests {
  def apply[A](implicit l: CellCodecLaws[A], al: Arbitrary[LegalValue[String, A]]): CellCodecTests[A] =
    CodecTests[String, A, CsvError, CellDecoder, CellEncoder]
}
