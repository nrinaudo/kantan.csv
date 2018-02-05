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

import kantan.codecs.{Encoder, EncoderCompanion}
import kantan.codecs.strings.StringEncoder

/** Provides useful methods for summoning and creating instances of [[CellEncoder]]. */
object CellEncoder extends EncoderCompanion[String, codecs.type] with PlatformSpecificCellEncoderInstances

/** All default [[CellEncoder]] instances. */
trait CellEncoderInstances {

  /** Turns existing `StringEncoder` instances into [[CellEncoder]] ones.
    *
    * This provides support for most basic Scala types.
    *
    * @example
    * {{{
    * CellEncoder[Int].encode(123)
    * res1: String = 123
    * }}}
    */
  implicit def fromStringEncoder[A: StringEncoder]: CellEncoder[A] = StringEncoder[A].tag[codecs.type]

  /** Provides an instance of `CellEncoder[Option[A]]` for any type `A` that has an instance of [[CellEncoder]].
    *
    * @example
    * {{{
    * Some encoding
    * scala> CellEncoder[Option[Int]].encode(Some(123))
    * res1: String = 123
    *
    * // None encoding
    * scala> CellEncoder[Option[Int]].encode(None)
    * res2: String = ""
    * }}}
    */
  implicit def cellEncoderOpt[A: CellEncoder]: CellEncoder[Option[A]] = Encoder.optionalEncoder

  /** Provides an instance of `CellEncoder[Either[A, B]]` for any type `A` and `B` that have instances of
    * [[CellEncoder]].
    *
    * @example
    * {{{
    * // Left encoding
    * scala> CellEncoder[Either[Int, Boolean]].encode(Left(123))
    * res1: String = 123
    *
    * // Right encoding
    * scala> CellEncoder[Either[Int, Boolean]].encode(Right(true))
    * res2: String = true
    * }}}
    */
  implicit def eitherCellEncoder[A: CellEncoder, B: CellEncoder]: CellEncoder[Either[A, B]] = Encoder.eitherEncoder
}
