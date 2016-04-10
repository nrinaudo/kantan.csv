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

import kantan.codecs.Encoder
import kantan.codecs.strings._

/** Provides useful methods for summoning and creating instances of [[CellEncoder]]. */
object CellEncoder {
  /** Summons an instance of [[CellEncoder]] if an implicit one can be found in scope.
    *
    * This is essentially a shorter way of calling `implicitly[CellEncoder[A]]`.
    */
  def apply[A](implicit ea: CellEncoder[A]): CellEncoder[A] = ea

  /** Creates a new [[CellEncoder]] from the specified function. */
  def apply[A](f: A ⇒ String): CellEncoder[A] = Encoder(f)
}

/** All default [[CellEncoder]] instances. */
trait CellEncoderInstances {
  /** Turns existing `StringEncoder` instances into [[CellEncoder]] ones. */
  implicit def fromStringEncoder[A](implicit ea: StringEncoder[A]): CellEncoder[A] = ea.tag[codecs.type]
}
