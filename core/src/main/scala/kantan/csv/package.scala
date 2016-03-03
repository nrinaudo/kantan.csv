package kantan

import kantan.codecs.{Codec, Encoder, Decoder, Result}

package object csv {
  type CsvResult[A] = Result[CsvError, A]
  type ParseResult[A] = Result[ParseError, A]
  type DecodeResult[A] = Result[DecodeError, A]

  type CellDecoder[A] = Decoder[String, A, DecodeError, Codecs.type]
  type CellEncoder[A] = Encoder[String, A, Codecs.type]
  type CellCodec[A] = Codec[String, A, DecodeError, Codecs.type]

  type RowDecoder[A] = Decoder[Seq[String], A, DecodeError, Codecs.type]
  type RowEncoder[A] = Encoder[Seq[String], A, Codecs.type]
  type RowCodec[A] = Codec[Seq[String], A, DecodeError, Codecs.type]
}
