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

package kantan.csv

import kantan.codecs.Decoder
import kantan.codecs.DecoderCompanion
import kantan.codecs.strings.StringDecoder
import kantan.csv.DecodeError.TypeError

/** Provides useful methods for summoning and creating instances of [[CellDecoder]]. */
object CellDecoder extends DecoderCompanion[String, DecodeError, codecs.type] with PlatformSpecificCellDecoderInstances

/** All default [[CellDecoder]] instances. */
trait CellDecoderInstances {

  /** Turns existing `StringDecoder` instances into [[CellDecoder]] ones.
    *
    * This provides support for most basic Scala types.
    *
    * @example
    *   {{{
    * scala> CellDecoder[Int].decode("123")
    * res1: DecodeResult[Int] = Right(123)
    *   }}}
    */
  implicit def fromStringDecoder[A: StringDecoder]: CellDecoder[A] =
    StringDecoder[A].tag[codecs.type].leftMap(e => TypeError(e.getMessage, e.getCause))

  /** Provides an instance of `CellDecoder[Option[A]]` for any type `A` that has an instance of [[CellDecoder]].
    *
    * @example
    *   {{{
    * // Non-empty value
    * scala> CellDecoder[Option[Int]].decode("123")
    * res1: DecodeResult[Option[Int]] = Right(Some(123))
    *
    * // Empty value
    * scala> CellDecoder[Option[Int]].decode("")
    * res2: DecodeResult[Option[Int]] = Right(None)
    *   }}}
    */
  implicit def cellDecoderOpt[A: CellDecoder]: CellDecoder[Option[A]] =
    Decoder.optionalDecoder

  /** Provides an instance of `CellDecoder[Either[A, B]]` for any type `A` and `B` that have instances of
    * [[CellDecoder]].
    *
    * @example
    *   {{{
    * // Left value
    * scala> CellDecoder[Either[Int, Boolean]].decode("123")
    * res1: DecodeResult[Either[Int, Boolean]] = Right(Left(123))
    *
    * // Right value
    * scala> CellDecoder[Either[Int, Boolean]].decode("true")
    * res2: DecodeResult[Either[Int, Boolean]] = Right(Right(true))
    *   }}}
    */
  implicit def cellDecoderEither[A: CellDecoder, B: CellDecoder]: CellDecoder[Either[A, B]] =
    Decoder.eitherDecoder
}
