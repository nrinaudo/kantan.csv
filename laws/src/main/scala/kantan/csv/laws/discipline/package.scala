package kantan.csv.laws

import kantan.codecs.laws.discipline.{CodecTests, EncoderTests, DecoderTests}
import kantan.csv._

package object discipline {
  type RowDecoderTests[A] = DecoderTests[Seq[String], A, DecodeError, Codecs.type]
  type RowEncoderTests[A] = EncoderTests[Seq[String], A, Codecs.type]
  type RowCodecTests[A] = CodecTests[Seq[String], A, DecodeError, Codecs.type]

  type CellDecoderTests[A] = DecoderTests[String, A, DecodeError, Codecs.type]
  type CellEncoderTests[A] = EncoderTests[String, A, Codecs.type]
  type CellCodecTests[A] = CodecTests[String, A, DecodeError, Codecs.type]
}
