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

/** Provides various instance creation and summoning methods.
  *
  * The instance creation functions are important to know about, as they make the task of creating new encoders easier
  * and more correct. There are four main families, depending on the type to encode:
  *
  *  - [[encoder1 encoderXXX]]: creates encoders from a function of arity `XXX` and for which you need to specify
  *    a mapping ''row index to parameter'' (such as if you need to skip some CSV cells, for instance).
  *  - [[ordered1 orderedXXX]]: create encoders from a function of arity `XXX` such that its parameters are organised
  *    in exactly the same way as CSV rows.
  *  - [[caseEncoder1 caseEncoderXXX]]: specialisation of [[encoder1 encoderXXX]] for case classes of arity `XXX`.
  *  - [[caseOrdered1 caseOrderedXXX]]: specialisation of [[ordered1 orderedXXX]] for case classes of arity `XXX`.
  *
  * Note that a lot of types already have implicit instances: tuples, collections... moreover, the `generics` module
  * can automatically derive valid instances for a lot of common scenarios.
  */
object RowEncoder extends GeneratedRowEncoders {
  /** Summons an implicit instance of [[RowEncoder]] for the desired type if one can be found.
    *
    * This is essentially a shorter way of calling `implicitly[RowEncoder[A]]`.
    */
  def apply[A](implicit ea: RowEncoder[A]): RowEncoder[A] = ea

  /** Creates a new [[RowEncoder]] using the specified function for encoding. */
  def apply[A](f: A ⇒ Seq[String]): RowEncoder[A] = Encoder(f)
}

/** Provides reasonable default [[RowEncoder]] instances for various types. */
trait RowEncoderInstances {
  /** Turns a [[CellEncoder]] into a [[RowEncoder]], for rows that contain a single value. */
  implicit def fromCellEncoder[A](implicit ea: CellEncoder[A]): RowEncoder[A] = RowEncoder(a ⇒ Seq(ea.encode(a)))

  /** Provides a [[RowEncoder]] instance for all traversable collections.
    *
    * Note that the elements of the collections are expected to have a [[CellEncoder]].
    */
  implicit def traversable[A, M[X] <: TraversableOnce[X]](implicit ea: CellEncoder[A]): RowEncoder[M[A]] =
    RowEncoder { as ⇒ as.foldLeft(Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result() }

  /** Provides a [[RowEncoder]] instance for `Either`, provided both alternatives have a [[CellEncoder]]. */
  implicit def eitherRowEncoder[A, B](implicit ea: RowEncoder[A], eb: RowEncoder[B]): RowEncoder[Either[A, B]] =
    RowEncoder { ss ⇒ ss match {
      case Left(a) ⇒ ea.encode(a)
      case Right(b) ⇒ eb.encode(b)
    }}

  /** Provides a [[RowEncoder]] instance for `Option`, provided the inner type has a [[CellEncoder]]. */
  implicit def optionRowEncoder[A](implicit ea: RowEncoder[A]): RowEncoder[Option[A]] =
    RowEncoder(_.map(a ⇒ ea.encode(a)).getOrElse(Seq.empty))
}
