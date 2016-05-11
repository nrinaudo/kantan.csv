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

import _root_.scalaz._
import _root_.scalaz.Maybe._
import _root_.scalaz.Scalaz._

/** Declares various type class instances for bridging `kantan.csv` and `scalaz`. */
package object scalaz extends kantan.codecs.scalaz.ScalazInstances {
  implicit def eitherRowDecoder[A, B](implicit da: RowDecoder[A], db: RowDecoder[B]): RowDecoder[A \/ B] =
    RowDecoder(row ⇒ da.decode(row).map(_.left[B]).orElse(db.decode(row).map(_.right[A])))

  implicit def maybeRowDecoder[A](implicit da: RowDecoder[A]): RowDecoder[Maybe[A]] = RowDecoder { row ⇒
    if(row.isEmpty) DecodeResult.success(empty)
    else da.decode(row).map(just)
  }

  implicit def eitherRowEncoder[A, B](implicit ea: RowEncoder[A], eb: RowEncoder[B]): RowEncoder[A \/ B] =
    RowEncoder(_.fold(ea.encode, eb.encode))

  implicit def foldableRowEncoder[F[_], A](implicit ea: CellEncoder[A], F: Foldable[F]): RowEncoder[F[A]] =
    RowEncoder(as ⇒ F.foldLeft(as, Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result())

  implicit def maybeRowEncoder[A](implicit ea: RowEncoder[A]): RowEncoder[Maybe[A]] = new RowEncoder[Maybe[A]] {
    override def encode(a: Maybe[A]) = a.map(ea.encode).getOrElse(Seq.empty)
  }

  // - CSV input / output ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Contravariant` instance for `CsvInput`. */
  implicit val csvInput: Contravariant[CsvInput] = new Contravariant[CsvInput] {
    override def contramap[A, B](r: CsvInput[A])(f: B ⇒ A) = r.contramap(f)
  }

  /** `Contravariant` instance for `CsvOutput`. */
  implicit val csvOutput: Contravariant[CsvOutput] = new Contravariant[CsvOutput] {
    override def contramap[A, B](r: CsvOutput[A])(f: B ⇒ A) = r.contramap(f)
  }

  // - ReadError -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val readErrorEqual: Equal[ReadError] = Equal.equalA[ReadError]
  implicit val decodeErrorEqual: Equal[DecodeError] = Equal.equalA[DecodeError]
  implicit val parseErrorEqual: Equal[ParseError] = Equal.equalA[ParseError]
}
