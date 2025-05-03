/*
 * Copyright 2015 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan

import kantan.codecs.Codec
import kantan.codecs.Decoder
import kantan.codecs.Encoder
import kantan.codecs.resource.ResourceIterator

package object csv extends HeaderDecoderOps0 {

  /** Iterator on CSV data.
    *
    * @documentable
    */
  type CsvReader[+A] = ResourceIterator[A]

  val rfc: CsvConfiguration = CsvConfiguration.rfc

  // - Results ---------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  /** Result of a reading operation.
    *
    * Both [[kantan.csv.ParseResult]] and [[DecodeResult]] are valid values of type [[ReadResult]].
    *
    * @documentable
    */
  type ReadResult[+A] = Either[ReadError, A]

  /** Result of a parsing operation.
    *
    * The difference between a [[ParseResult parse]] and a [[DecodeResult decode]] result is that the former comes from
    * reading raw data and trying to interpret it as CSV, while the later comes from turning CSV data into useful Scala
    * types.
    *
    * Failure cases are all encoded as [[ParseError]].
    *
    * @documentable
    */
  type ParseResult[+A] = Either[ParseError, A]

  /** Result of a decode operation.
    *
    * The difference between a [[ParseResult parse]] and a [[DecodeResult decode]] result is that the former comes from
    * reading raw data and trying to interpret it as CSV, while the later comes from turning CSV data into useful Scala
    * types.
    *
    * Failure cases are all encoded as [[DecodeError]].
    *
    * @documentable
    */
  type DecodeResult[+A] = Either[DecodeError, A]

  // - Cell codecs -----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Describes how to decode CSV cells into specific types.
    *
    * All types `A` such that there exists an implicit instance of `CellDecoder[A]` in scope can be decoded from CSV
    * cells.
    *
    * Note that instances of this type class are rarely used directly - their purpose is to be implicitly assembled into
    * more complex instances of [[kantan.csv.RowDecoder]].
    *
    * See the [[CellDecoder$ companion object]] for creation and summoning methods.
    *
    * @tparam A
    *   type this instance know to decode from.
    * @see
    *   kantan.codecs.Decoder
    * @documentable
    */
  type CellDecoder[A] = Decoder[String, A, DecodeError, codecs.type]

  /** Describes how to encode values of a specific type to CSV cells.
    *
    * All types `A` such that there exists an implicit instance of `CellEncoder[A]` in scope can be encoded to CSV
    * cells.
    *
    * Note that instances of this type class are rarely used directly - their purpose is to be implicitly assembled into
    * more complex instances of [[RowEncoder]].
    *
    * See the [[CellEncoder$ companion object]] for creation and summoning methods.
    *
    * @tparam A
    *   type this instance knows to encode to.
    * @see
    *   kantan.codecs.Encoder
    * @documentable
    */
  type CellEncoder[A] = Encoder[String, A, codecs.type]

  /** Aggregates a [[CellEncoder]] and a [[CellDecoder]].
    *
    * The sole purpose of this type class is to provide a convenient way to create encoders and decoders. It should not
    * be used directly for anything but instance creation - in particular, it should never be used in a context bound or
    * expected as an implicit parameter.
    *
    * @see
    *   kantan.codecs.Codec
    * @documentable
    */
  type CellCodec[A] = Codec[String, A, DecodeError, codecs.type]

  // - Row codecs ------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Describes how to decode CSV rows into specific types.
    *
    * See the [[RowDecoder$ companion object]] for creation and summoning methods.
    *
    * @tparam A
    *   type this instance know to decode from.
    * @see
    *   kantan.codecs.Decoder
    * @documentable
    */
  type RowDecoder[A] = Decoder[Seq[String], A, DecodeError, codecs.type]

  /** Describes how to encode values of a specific type to CSV rows.
    *
    * See the [[RowEncoder$ companion object]] for creation and summoning methods.
    *
    * @tparam A
    *   type this instance knows to encode to.
    * @see
    *   kantan.codecs.Encoder
    * @documentable
    */
  type RowEncoder[A] = Encoder[Seq[String], A, codecs.type]

  /** Aggregates a [[RowEncoder]] and a [[RowDecoder]].
    *
    * The sole purpose of this type class is to provide a convenient way to create encoders and decoders. It should not
    * be used directly for anything but instance creation - in particular, it should never be used in a context bound or
    * expected as an implicit parameter.
    *
    * @see
    *   kantan.codecs.Codec
    * @documentable
    */
  type RowCodec[A] = Codec[Seq[String], A, DecodeError, codecs.type]
}
