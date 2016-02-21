package kantan.csv.laws.discipline

import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.discipline.DecoderTests
import kantan.csv.laws.RowDecoderLaws
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.{CsvError, RowDecoder}
import org.scalacheck.Arbitrary

object RowDecoderTests {
  def apply[A](implicit l: RowDecoderLaws[A], al: Arbitrary[LegalValue[Seq[String], A]]): RowDecoderTests[A] =
    DecoderTests[Seq[String], A, CsvError, RowDecoder]
}
