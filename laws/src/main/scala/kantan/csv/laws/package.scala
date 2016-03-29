package kantan.csv

import kantan.codecs.laws.{CodecLaws, CodecValue, DecoderLaws, EncoderLaws}
import kantan.codecs.laws.CodecValue.LegalValue

package object laws {
  type CellDecoderLaws[A] = DecoderLaws[String, A, DecodeError, codecs.type]
  type CellEncoderLaws[A] = EncoderLaws[String, A, codecs.type]
  type CellCodecLaws[A] = CodecLaws[String, A, DecodeError, codecs.type]
  type RowDecoderLaws[A] = DecoderLaws[Seq[String], A, DecodeError, codecs.type]
  type RowEncoderLaws[A] = EncoderLaws[Seq[String], A, codecs.type]
  type RowCodecLaws[A] = CodecLaws[Seq[String], A, DecodeError, codecs.type]

  type CellValue[A] = CodecValue[String, A]
  type LegalCell[A] = LegalValue[String, A]
  type IllegalCell[A] = CodecValue.IllegalValue[String, A]
  type RowValue[A] = CodecValue[Seq[String], A]
  type LegalRow[A] = LegalValue[Seq[String], A]
  type IllegalRow[A] = CodecValue.IllegalValue[Seq[String], A]
}
