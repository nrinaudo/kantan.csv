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

import kantan.csv.{CellDecoder, DecodeResult, _}
import shapeless.{HNil, _}

trait GenericInstances {
  // - Coproduct decoders ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductCellDecoder[H, T <: Coproduct]
  (implicit dh: CellDecoder[H], dt: CellDecoder[T]): CellDecoder[H :+: T] =
    CellDecoder(row ⇒ dh.decode(row).map(Inl.apply).orElse(dt.decode(row).map(Inr.apply)))

  implicit val cnilCellDecoder: CellDecoder[CNil] = CellDecoder(c ⇒ DecodeResult.typeError(s"Not a legal cell: $c"))

  implicit def coproductRowDecoder[H, T <: Coproduct]
  (implicit dh: RowDecoder[H], dt: RowDecoder[T]): RowDecoder[H :+: T] =
    RowDecoder(row ⇒ dh.decode(row).map(Inl.apply).orElse(dt.decode(row).map(Inr.apply)))

  implicit val cnilRowDecoder: RowDecoder[CNil] = RowDecoder(r ⇒ DecodeResult.typeError(s"Not a legal row: $r"))



  // - Coproduct encoders ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductCellEncoder[H, T <: Coproduct]
  (implicit eh: CellEncoder[H], et: CellEncoder[T]): CellEncoder[H :+: T] =
    CellEncoder((a: H :+: T) ⇒ a match {
      case Inl(h) ⇒ eh.encode(h)
      case Inr(t) ⇒ et.encode(t)
    })

  implicit val cnilCellEncoder: CellEncoder[CNil] =
    CellEncoder((_: CNil) ⇒ sys.error("trying to encode CNil, this should not happen"))

  implicit def coproductRowEncoder[H, T <: Coproduct]
  (implicit eh: RowEncoder[H], et: RowEncoder[T]): RowEncoder[H :+: T] =
    RowEncoder((a: H :+: T) ⇒ a match {
      case Inl(h) ⇒ eh.encode(h)
      case Inr(t) ⇒ et.encode(t)
    })

  implicit val cnilRowEncoder: RowEncoder[CNil] =
    RowEncoder((_: CNil) ⇒ sys.error("trying to encode CNil, this should not happen"))


  // - HList decoders --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistSingletonRowDecoder[H](implicit dh: RowDecoder[H]): RowDecoder[H :: HNil] =
    dh.map(h ⇒ h :: HNil)

  implicit def hlistRowDecoder[H, T <: HList](implicit dh: CellDecoder[H], dt: RowDecoder[T]): RowDecoder[H :: T] =
    RowDecoder(row ⇒
      row.headOption.map(s ⇒
        for {
          h ← dh.decode(s)
          t ← dt.decode(row.tail)
        } yield h :: t
      ).getOrElse(DecodeResult.outOfBounds(0)))


  implicit def hlistCellDecoder[H](implicit dh: CellDecoder[H]): CellDecoder[H :: HNil] = dh.map(h ⇒ h :: HNil)

  implicit val hnilRowDecoder: RowDecoder[HNil] = RowDecoder(_ ⇒ DecodeResult.success(HNil))



  // - HList encoders --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistSingletonRowEncoder[H](implicit eh: RowEncoder[H]): RowEncoder[H :: HNil] = eh.contramap {
    case (h :: _) ⇒ h
  }

  implicit def hlistRowEncoder[H, T <: HList](implicit eh: CellEncoder[H], et: RowEncoder[T]): RowEncoder[H :: T] =
    RowEncoder((a: H :: T) ⇒ a match {
      case h :: t ⇒ eh.encode(h) +: et.encode(t)
    })

  implicit def hlistCellEncoder[H](implicit eh: CellEncoder[H]): CellEncoder[H :: HNil] = eh.contramap {
    case (h :: _) ⇒ h
  }

  implicit val hnilRowEncoder: RowEncoder[HNil] = RowEncoder(_ ⇒ Seq.empty)
}
