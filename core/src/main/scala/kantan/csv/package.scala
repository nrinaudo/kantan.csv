package kantan

import kantan.codecs.{Codec, Decoder, Encoder, Result}

package object csv {
  type CsvResult[A] = Result[CsvError, A]
  type ParseResult[A] = Result[ParseError, A]
  type DecodeResult[A] = Result[DecodeError, A]

  type CellDecoder[A] = Decoder[String, A, DecodeError, codecs.type]
  type CellEncoder[A] = Encoder[String, A, codecs.type]
  type CellCodec[A] = Codec[String, A, DecodeError, codecs.type]

  type RowDecoder[A] = Decoder[Seq[String], A, DecodeError, codecs.type]
  type RowEncoder[A] = Encoder[Seq[String], A, codecs.type]
  type RowCodec[A] = Codec[Seq[String], A, DecodeError, codecs.type]
}
