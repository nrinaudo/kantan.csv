package kantan.csv.laws

import kantan.codecs.laws.discipline.{CodecTests, DecoderTests, EncoderTests}
import kantan.csv._

package object discipline {
  type RowDecoderTests[A] = DecoderTests[Seq[String], A, DecodeError, codecs.type]
  type RowEncoderTests[A] = EncoderTests[Seq[String], A, codecs.type]
  type RowCodecTests[A] = CodecTests[Seq[String], A, DecodeError, codecs.type]

  type CellDecoderTests[A] = DecoderTests[String, A, DecodeError, codecs.type]
  type CellEncoderTests[A] = EncoderTests[String, A, codecs.type]
  type CellCodecTests[A] = CodecTests[String, A, DecodeError, codecs.type]
}
