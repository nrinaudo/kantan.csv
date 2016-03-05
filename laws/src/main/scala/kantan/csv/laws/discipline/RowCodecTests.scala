package kantan.csv.laws.discipline

import kantan.codecs.laws.CodecValue.LegalValue
import kantan.codecs.laws.discipline.CodecTests
import kantan.csv.laws._
import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.{codecs, DecodeError}
import org.scalacheck.Arbitrary

object RowCodecTests {
  def apply[A](implicit l: RowCodecLaws[A], al: Arbitrary[LegalValue[Seq[String], A]]): RowCodecTests[A] =
    CodecTests[Seq[String], A, DecodeError, codecs.type]
}
