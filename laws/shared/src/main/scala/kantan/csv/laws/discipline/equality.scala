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

package kantan.csv.laws.discipline

import kantan.csv.CellDecoder
import kantan.csv.CellEncoder
import kantan.csv.DecodeResult
import kantan.csv.RowDecoder
import kantan.csv.RowEncoder
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.{arbitrary => arb}

object equality {
  @SuppressWarnings(Array("org.wartremover.warts.PublicInference"))
  def eq[A, B: Arbitrary](a1: B => A, a2: B => A)(f: (A, A) => Boolean): Boolean = {
    val samples: List[B] = List.fill(100)(arb[B].sample).collect {
      case Some(a) => a
      case None    => sys.error("Could not generate arbitrary values to compare two functions")
    }
    samples.forall(b => f(a1(b), a2(b)))
  }

  def cellDecoder[A](c1: CellDecoder[A], c2: CellDecoder[A])(
    f: (DecodeResult[A], DecodeResult[A]) => Boolean
  ): Boolean =
    eq(c1.decode, c2.decode)(f)

  def cellEncoder[A: Arbitrary](c1: CellEncoder[A], c2: CellEncoder[A]): Boolean =
    eq(c1.encode, c2.encode)(_ == _)

  def rowDecoder[A](c1: RowDecoder[A], c2: RowDecoder[A])(f: (DecodeResult[A], DecodeResult[A]) => Boolean): Boolean =
    eq(c1.decode, c2.decode)(f)

  def rowEncoder[A: Arbitrary](c1: RowEncoder[A], c2: RowEncoder[A]): Boolean =
    eq(c1.encode, c2.encode)(_ == _)
}
