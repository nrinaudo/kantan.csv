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

import _root_.scalaz._
import _root_.scalaz.Maybe._
import _root_.scalaz.Scalaz._
import imp.imp

/** Declares various type class instances for bridging `kantan.csv` and `scalaz`. */
package object scalaz extends kantan.codecs.scalaz.ScalazInstances {
  implicit def eitherRowDecoder[A: RowDecoder, B: RowDecoder]: RowDecoder[A \/ B] =
    RowDecoder.from(row ⇒ RowDecoder[A].decode(row).map(_.left[B])
      .orElse(RowDecoder[B].decode(row).map(_.right[A])))

  implicit def maybeRowDecoder[A: RowDecoder]: RowDecoder[Maybe[A]] = RowDecoder.from { row ⇒
    if(row.isEmpty) DecodeResult.success(empty)
    else            RowDecoder[A].decode(row).map(just)
  }

  implicit def eitherRowEncoder[A: RowEncoder, B: RowEncoder]: RowEncoder[A \/ B] =
    RowEncoder.from(_.fold(RowEncoder[A].encode, RowEncoder[B].encode))

  implicit def foldableRowEncoder[F[_]: Foldable, A: CellEncoder]: RowEncoder[F[A]] =
    RowEncoder.from(as ⇒ imp[Foldable[F]]
      .foldLeft(as, Seq.newBuilder[String])((acc, a) ⇒ acc += CellEncoder[A].encode(a)).result())

  implicit def maybeRowEncoder[A: RowEncoder]: RowEncoder[Maybe[A]] = new RowEncoder[Maybe[A]] {
    override def encode(a: Maybe[A]): Seq[String] = a.map(RowEncoder[A].encode).getOrElse(Seq.empty)
  }



  // - CSV input / output ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Contravariant` instance for `CsvSource`. */
  implicit val csvSource: Contravariant[CsvSource] = new Contravariant[CsvSource] {
    override def contramap[A, B](r: CsvSource[A])(f: B ⇒ A): CsvSource[B] = r.contramap(f)
  }

  /** `Contravariant` instance for `CsvSink`. */
  implicit val csvSink: Contravariant[CsvSink] = new Contravariant[CsvSink] {
    override def contramap[A, B](r: CsvSink[A])(f: B ⇒ A): CsvSink[B] = r.contramap(f)
  }

  // - ReadError -------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val readErrorEqual: Equal[ReadError] = Equal.equalA[ReadError]
  implicit val decodeErrorEqual: Equal[DecodeError] = Equal.equalA[DecodeError]
  implicit val parseErrorEqual: Equal[ParseError] = Equal.equalA[ParseError]
}
