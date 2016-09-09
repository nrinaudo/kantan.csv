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

package kantan.csv.generic

import kantan.codecs.shapeless.ShapelessInstances
import kantan.csv.{CellDecoder, DecodeResult, _}
import shapeless.{HNil, _}

trait GenericInstances extends ShapelessInstances {
  // - Coproduct decoders ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val cnilCellDecoder: CellDecoder[CNil] = cnilDecoder(c ⇒ DecodeError.TypeError(s"Not a legal cell: $c"))
  implicit val cnilRowDecoder: RowDecoder[CNil] = cnilDecoder(r ⇒ DecodeError.TypeError(s"Not a legal row: $r"))



  // - HList decoders --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistSingletonRowDecoder[H](implicit dh: RowDecoder[H]): RowDecoder[H :: HNil] =
    dh.map(h ⇒ h :: HNil)

  implicit def hlistRowDecoder[H, T <: HList](implicit dh: CellDecoder[H], dt: RowDecoder[T]): RowDecoder[H :: T] =
    RowDecoder.from(row ⇒
      row.headOption.map(s ⇒
        for {
          h ← dh.decode(s)
          t ← dt.decode(row.tail)
        } yield h :: t
      ).getOrElse(DecodeResult.outOfBounds(0)))


  implicit def hlistCellDecoder[H](implicit dh: CellDecoder[H]): CellDecoder[H :: HNil] = dh.map(h ⇒ h :: HNil)

  implicit val hnilRowDecoder: RowDecoder[HNil] = RowDecoder.from(_ ⇒ DecodeResult.success(HNil))



  // - HList encoders --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistSingletonRowEncoder[H](implicit eh: RowEncoder[H]): RowEncoder[H :: HNil] = eh.contramap {
    case (h :: _) ⇒ h
  }

  implicit def hlistRowEncoder[H, T <: HList](implicit eh: CellEncoder[H], et: RowEncoder[T]): RowEncoder[H :: T] =
    RowEncoder.from {
      case h :: t ⇒ eh.encode(h) +: et.encode(t)
    }

  implicit def hlistCellEncoder[H](implicit eh: CellEncoder[H]): CellEncoder[H :: HNil] = eh.contramap {
    case (h :: _) ⇒ h
  }

  implicit val hnilRowEncoder: RowEncoder[HNil] = RowEncoder.from(_ ⇒ Seq.empty)
}
