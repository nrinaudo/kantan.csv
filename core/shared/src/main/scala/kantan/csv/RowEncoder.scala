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

import kantan.codecs.EncoderCompanion

/** Provides various instance creation and summoning methods.
  *
  * The instance creation functions are important to know about, as they make the task of creating new encoders easier
  * and more correct. There are four main families, depending on the type to encode:
  *
  *   - `encoder`: creates encoders from a function for which you need to specify a mapping ''row index to parameter''
  *     (such as if you need to skip some CSV cells, for instance).
  *   - `ordered`: create encoders from a function such that its parameters are organised in exactly the same way as CSV
  *     rows.
  *   - `caseEncoder`: specialisation of `encoder` for case classes.
  *   - `caseOrdered`: specialisation of `ordered` for case classes.
  *
  * Note that a lot of types already have implicit instances: tuples, collections... moreover, the `generics` module can
  * automatically derive valid instances for a lot of common scenarios.
  */
object RowEncoder extends GeneratedRowEncoders with EncoderCompanion[Seq[String], codecs.type]

/** Provides reasonable default [[RowEncoder]] instances for various types. */
trait RowEncoderInstances extends VersionSpecificRowEncoderInstances {

  /** Turns a [[CellEncoder]] into a [[RowEncoder]], for rows that contain a single value.
    *
    * {{{
    * scala> RowEncoder[Int].encode(123)
    * res1: Seq[String] = List(123)
    * }}}
    */
  implicit def fromCellEncoder[A: CellEncoder]: RowEncoder[A] =
    RowEncoder.from(a => Seq(CellEncoder[A].encode(a)))

}
