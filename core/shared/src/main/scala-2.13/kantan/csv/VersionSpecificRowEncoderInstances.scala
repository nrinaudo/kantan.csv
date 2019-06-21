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

trait VersionSpecificRowEncoderInstances {

  /** Provides a [[RowEncoder]] instance for all traversable collections.
    *
    * `List`, for example:
    * {{{
    * scala> RowEncoder[List[Int]].encode(List(123, 456, 789))
    * res1: Seq[String] = List(123, 456, 789)
    * }}}
    */
  implicit def iterable[A: CellEncoder, M[X] <: IterableOnce[X]]: RowEncoder[M[A]] =
    RowEncoder.from(_.iterator.foldLeft(Seq.newBuilder[String])((acc, a) => acc += CellEncoder[A].encode(a)).result())
}
