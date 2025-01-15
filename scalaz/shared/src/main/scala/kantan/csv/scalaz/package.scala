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

import _root_.scalaz.Equal
import _root_.scalaz.Foldable
import imp.imp
import kantan.codecs.scalaz.CommonInstances
import kantan.codecs.scalaz.DecoderInstances
import kantan.codecs.scalaz.EncoderInstances

/** Declares various type class instances for bridging `kantan.csv` and `scalaz`. */
package object scalaz extends DecoderInstances with EncoderInstances with CommonInstances {

  // - Eq instances ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  implicit val csvOutOfBoundsEqual: Equal[DecodeError.OutOfBounds]         = Equal.equalA
  implicit val csvTypeErrorEqual: Equal[DecodeError.TypeError]             = Equal.equalA
  implicit val csvDecodeErrorEqual: Equal[DecodeError]                     = Equal.equalA
  implicit val csvNoSuchElementEqual: Equal[ParseError.NoSuchElement.type] = Equal.equalA
  implicit val csvIoErrorEqual: Equal[ParseError.IOError]                  = Equal.equalA
  implicit val csvParseErrorEqual: Equal[ParseError]                       = Equal.equalA
  implicit val csvReadErrorEqual: Equal[ReadError]                         = Equal.equalA

  implicit def foldableRowEncoder[F[_]: Foldable, A: CellEncoder]: RowEncoder[F[A]] =
    RowEncoder.from { as =>
      imp[Foldable[F]]
        .foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += CellEncoder[A].encode(a))
        .result()
    }

}
