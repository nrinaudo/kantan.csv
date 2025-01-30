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

import kantan.codecs.DecoderCompanion
import kantan.codecs.collection.Factory

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

  /** Decodes the cell found at the specified index of `ss` into the requested type.
    *
    * @example
    * {{{
    * scala> RowDecoder.decodeCell[Int](List("abc", "123"), 1)
    * res0: DecodeResult[Int] = Right(123)
    *
    * scala> RowDecoder.decodeCell[Int](List("abc", "123"), 0)
    * res0: DecodeResult[Int] = Left(TypeError: 'abc' is not a valid Int)
    * }}}
    */
  @SuppressWarnings(Array("org.wartremover.warts.SeqApply"))
  def decodeCell[A: CellDecoder](ss: Seq[String], i: Int): DecodeResult[A] =
    if(ss.isDefinedAt(i)) CellDecoder[A].decode(ss(i))
    // Special case, see https://github.com/nrinaudo/kantan.csv/issues/53
    else if(i == ss.length) CellDecoder[A].decode("")
    else DecodeResult.outOfBounds(i)

  /** Provides a [[RowDecoder]] instance that decodes a single cell from each row.
    *
    * @example
    * {{{
    * RowDecoder.field[Int](1).decode(Seq("123", "456", "789"))
    * res1: DecodeResult[Int] = Right(456)
    * }}}
    */
  def field[A: CellDecoder](index: Int): RowDecoder[A] = RowDecoder.from { ss =>
    ss.lift(index).map(CellDecoder[A].decode).getOrElse(DecodeResult.outOfBounds(index))
  }
}

/** Provides reasonable default [[RowDecoder]] instances for various types. */
trait RowDecoderInstances {

  /** Turns a [[CellDecoder]] into a [[RowDecoder]], for rows that contain a single value.
    *
    * This provides default behaviour for [[RowDecoder.field]] by decoding the first cell.
    *
    * @example
    * {{{
    * RowDecoder[Int].decode(Seq("123", "456", "789"))
    * res1: DecodeResult[Int] = Right(123)
    * }}}
    */
  implicit def fromCellDecoder[A: CellDecoder]: RowDecoder[A] = RowDecoder.field[A](0)

  /** Provides a [[RowDecoder]] instance for all types that have an `Factory`, provided the inner type has a
    * [[CellDecoder]].
    *
    * @example
    * {{{
    * RowDecoder[List[Int]].decode(Seq("123", "456", "789"))
    * res1: DecodeResult[List[Int]] = Right(List(123, 456, 789))
    * }}}
    */
  implicit def hasBuilderRowDecoder[A: CellDecoder, F[_]](implicit hb: Factory[A, F[A]]): RowDecoder[F[A]] =
    RowDecoder.from(_.foldLeft(DecodeResult(hb.newBuilder)) { (racc, s) =>
      for {
        acc <- racc
        a   <- CellDecoder[A].decode(s)
      } yield acc += a
    }.map(_.result()))
}
