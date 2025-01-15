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

import _root_.cats.Eq
import _root_.cats.Foldable
import imp.imp
import kantan.codecs.cats.CommonInstances
import kantan.codecs.cats.DecoderInstances
import kantan.codecs.cats.EncoderInstances

/** Declares various type class instances for bridging `kantan.csv` and `cats`. */
package object cats extends CommonInstances with DecoderInstances with EncoderInstances {

  // - Eq instances ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit val csvOutOfBoundsEq: Eq[DecodeError.OutOfBounds]         = Eq.fromUniversalEquals
  implicit val csvTypeErrorEq: Eq[DecodeError.TypeError]             = Eq.fromUniversalEquals
  implicit val csvDecodeErrorEq: Eq[DecodeError]                     = Eq.fromUniversalEquals
  implicit val csvNoSuchElementEq: Eq[ParseError.NoSuchElement.type] = Eq.fromUniversalEquals
  implicit val csvIoErrorEq: Eq[ParseError.IOError]                  = Eq.fromUniversalEquals
  implicit val csvParseErrorEq: Eq[ParseError]                       = Eq.fromUniversalEquals
  implicit val csvReadErrorEq: Eq[ReadError]                         = Eq.fromUniversalEquals

  // - Misc. instances --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit def foldableRowEncoder[F[_]: Foldable, A: CellEncoder]: RowEncoder[F[A]] =
    RowEncoder.from { as =>
      imp[Foldable[F]]
        .foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += CellEncoder[A].encode(a))
        .result()
    }

}
