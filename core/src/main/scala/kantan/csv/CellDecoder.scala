/*
 * Copyright 2016 Nicolas Rinaudo
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

import kantan.codecs.{Decoder, DecoderCompanion}
import kantan.codecs.strings.StringDecoder
import kantan.csv.DecodeError.TypeError
import scala.language.experimental.macros

/** Provides useful methods for summoning and creating instances of [[CellDecoder]]. */
object CellDecoder extends DecoderCompanion[String, DecodeError, codecs.type] {
  /** Summons an instance of [[CellDecoder]] if an implicit one can be found in scope.
    *
    * This is essentially a shorter way of calling `implicitly[CellDecoder[A]]`.
    */
  def apply[A](implicit ev: CellDecoder[A]): CellDecoder[A] = macro imp.summon[CellDecoder[A]]
}

/** All default [[CellDecoder]] instances. */
trait CellDecoderInstances {
  /** Turns existing `StringDecoder` instances into [[CellDecoder]] ones. */
  implicit def fromStringDecoder[A: StringDecoder]: CellDecoder[A] =
    StringDecoder[A].tag[codecs.type].mapError(e ⇒ TypeError(e.getMessage, e.getCause))
  implicit def cellDecoderOpt[A: CellDecoder]: CellDecoder[Option[A]] = Decoder.optionalDecoder
  implicit def cellDecoderEither[A: CellDecoder, B: CellDecoder]: CellDecoder[Either[A, B]] = Decoder.eitherDecoder
}
