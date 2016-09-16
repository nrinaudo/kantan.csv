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

import _root_.cats._
import _root_.cats.data.Xor
import _root_.cats.functor.Contravariant
import imp.imp

/** Declares various type class instances for bridging `kantan.csv` and `cats`. */
package object cats extends kantan.codecs.cats.CatsInstances {
  implicit def xorRowDecoder[A: RowDecoder, B: RowDecoder]: RowDecoder[Xor[A, B]] =
    RowDecoder.from(row ⇒ RowDecoder[A].decode(row).map(a ⇒ Xor.Left(a))
      .orElse(RowDecoder[B].decode(row).map(b ⇒ Xor.Right(b))))

  implicit def xorRowEncoder[A: RowEncoder, B: RowEncoder]: RowEncoder[Xor[A, B]] =
    RowEncoder.from(_.fold(RowEncoder[A].encode, RowEncoder[B].encode))

  implicit def foldableRowEncoder[F[_]: Foldable, A: CellEncoder]: RowEncoder[F[A]] =
    RowEncoder.from(as ⇒ imp[Foldable[F]]
      .foldLeft(as, Seq.newBuilder[String])((acc, a) ⇒ acc += CellEncoder[A].encode(a)).result())



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
  implicit val readErrorEq: Eq[ReadError] = Eq.fromUniversalEquals[ReadError]
  implicit val decodeErrorEq: Eq[DecodeError] = Eq.fromUniversalEquals[DecodeError]
  implicit val parseErrorEq: Eq[ParseError] = Eq.fromUniversalEquals[ParseError]
}
