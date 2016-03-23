package kantan

import kantan.codecs.{Codec, Decoder, Encoder, Result}

package object csv {
  type CsvResult[A] = Result[CsvError, A]
  type ParseResult[A] = Result[ParseError, A]
  type DecodeResult[A] = Result[DecodeError, A]

  /** Describes how to decode CSV cells into specific types.
    *
    * All types `A` such that there exists an implicit instance of `CellDecoder[A]` in scope can be decoded from CSV
    * cells.
    *
    * Note that instances of this type class is rarely used directly - their purpose is to be implicitly assembled
    * into more complex instances of [[RowDecoder]].
    *
    * See the [[CellDecoder$ companion object]] for creation and summoning methods.
    *
    * @tparam A type this instance know to decode from.
    */
  type CellDecoder[A] = Decoder[String, A, DecodeError, codecs.type]

  /** Describes how to encoded values of a specific type to CSV cells.
    *
    * All types `A` such that there exists an implicit instance of `CellEncoder[A]` in scope can be encoded to CSV
    * cells.
    *
    * Note that instances of this type class is rarely used directly - their purpose is to be implicitly assembled
    * into more complex instances of [[RowEncoder]].
    *
    * See the [[CellEncoder$ companion object]] for creation and summoning methods.
    *
    * @tparam A type this instance knows to encode to.
    */
  type CellEncoder[A] = Encoder[String, A, codecs.type]

  /** Aggregates a [[CellEncoder]] and a [[CellDecoder]].
    *
    * The sole purpose of this type class is to provide a convenient way to create encoders and decoders. It should
    * not be used directly for anything but instance creation - in particular, it should never be used in a context
    * bound or expected as an implicit parameter.
    */
  type CellCodec[A] = Codec[String, A, DecodeError, codecs.type]

  type RowDecoder[A] = Decoder[Seq[String], A, DecodeError, codecs.type]
  type RowEncoder[A] = Encoder[Seq[String], A, codecs.type]
  type RowCodec[A] = Codec[Seq[String], A, DecodeError, codecs.type]
}
