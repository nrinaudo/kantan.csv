package kantan.csv.laws

import kantan.codecs.laws.discipline.{CodecTests, EncoderTests, DecoderTests}
import kantan.csv._

package object discipline {
  type RowDecoderTests[A] = DecoderTests[Seq[String], A, CsvError, RowDecoder]
  type RowEncoderTests[A] = EncoderTests[Seq[String], A, RowEncoder]
  type RowCodecTests[A] = CodecTests[Seq[String], A, CsvError, RowDecoder, RowEncoder]

  type CellDecoderTests[A] = DecoderTests[String, A, CsvError, CellDecoder]
  type CellEncoderTests[A] = EncoderTests[String, A, CellEncoder]
  type CellCodecTests[A] = CodecTests[String, A, CsvError, CellDecoder, CellEncoder]
}
