/*
 * Copyright 2017 Nicolas Rinaudo
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

import kantan.codecs.DecoderCompanion
import scala.collection.generic.CanBuildFrom

/** Provides various instance creation and summoning methods.
  *
  * The instance creation functions are important to know about, as they make the task of creating new decoders easier
  * and more correct. There are two main families, depending on the type to decode:
  *
  *  - `decoder`: creates decoders from a function of arity `XXX` and for which you need to specify
  *    a mapping ''parameter to row index'' (such as if the order in which cells are written doesn't match that of
  *    the function's parameters).
  *  - `ordered`: create decoders from a function of arity `XXX` such that its parameters are organised
  *    in exactly the same way as CSV rows.
  *
  * Note that a lot of types already have implicit instances: tuples, collections... moreover, the `generics` module
  * can automatically derive valid instances for a lot of common scenarios.
  */
object RowDecoder extends GeneratedRowDecoders with DecoderCompanion[Seq[String], DecodeError, codecs.type] {
  /** Summons an implicit instance of [[RowDecoder]] for the desired type if one can be found.
    *
    * This is essentially a shorter way of calling `implicitly[RowDecoder[A]]`.
    */
  def apply[A](implicit ev: RowDecoder[A]): RowDecoder[A] = macro imp.summon[RowDecoder[A]]
}

/** Provides reasonable default [[RowDecoder]] instances for various types. */
trait RowDecoderInstances {
  /** Turns a [[CellDecoder]] into a [[RowDecoder]], for rows that contain a single value. */
  implicit def fromCellDecoder[A: CellDecoder]: RowDecoder[A] = RowDecoder.from(ss ⇒
    ss.headOption.map(h ⇒ if(ss.tail.isEmpty) CellDecoder[A].decode(h) else DecodeResult.outOfBounds(1))
      .getOrElse(DecodeResult.outOfBounds(0))
  )

  /** Provides a [[RowDecoder]] instance for all types that have an `CanBuildFrom`, provided the inner type has a
    * [[CellDecoder]].
    */
  implicit def cbfRowDecoder[A: CellDecoder, M[X]](implicit cbf: CanBuildFrom[Nothing, A, M[A]]): RowDecoder[M[A]] =
    RowDecoder.from(_.foldLeft(DecodeResult(cbf.apply())) { (racc, s) ⇒ for {
      acc ← racc
      a   ← CellDecoder[A].decode(s)
    } yield acc += a
    }.map(_.result()))
}
