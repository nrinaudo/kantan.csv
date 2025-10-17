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

package kantan.csv.generic

import kantan.codecs.shapeless.ShapelessInstances
import kantan.csv.CellDecoder
import kantan.csv.CellEncoder
import kantan.csv.DecodeResult
import kantan.csv.HeaderEncoder
import kantan.csv.RowDecoder
import kantan.csv.RowEncoder
import shapeless.::
import shapeless.HList
import shapeless.HNil
import shapeless.LabelledGeneric
import shapeless.ops.hlist.ToList
import shapeless.ops.record.Keys

import scala.annotation.nowarn

trait GenericInstances extends ShapelessInstances {

  @nowarn("msg=parameter gen in method genHeaderEncoder is never used")
  implicit def genHeaderEncoder[T, Repr <: HList, Ks <: HList](
    implicit gen: LabelledGeneric.Aux[T, Repr],
             re: RowEncoder[T],
             keys: Keys.Aux[Repr, Ks],
             toList: ToList[Ks, Symbol]
  ): HeaderEncoder[T] = new HeaderEncoder[T] {

    override val header: Option[Seq[String]] = Some(keys().toList.map(_.name))

    override val rowEncoder: RowEncoder[T] = re
  }

  // - HList decoders --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistSingletonRowDecoder[H: RowDecoder]: RowDecoder[H :: HNil] =
    RowDecoder[H].map(h => h :: HNil)

  implicit def hlistRowDecoder[H: CellDecoder, T <: HList: RowDecoder]: RowDecoder[H :: T] =
    RowDecoder.from { row =>
      row.headOption.map { s =>
        for {
          h <- CellDecoder[H].decode(s)
          t <- RowDecoder[T].decode(row.drop(1))
        } yield h :: t
      }.getOrElse(DecodeResult.outOfBounds(0))
    }

  implicit def hlistCellDecoder[H: CellDecoder]: CellDecoder[H :: HNil] =
    CellDecoder[H].map(h => h :: HNil)

  implicit val hnilRowDecoder: RowDecoder[HNil] = RowDecoder.from(_ => DecodeResult.success(HNil))

  // - HList encoders --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistSingletonRowEncoder[H: RowEncoder]: RowEncoder[H :: HNil] =
    RowEncoder[H].contramap { case (h :: _) =>
      h
    }

  implicit def hlistRowEncoder[H: CellEncoder, T <: HList: RowEncoder]: RowEncoder[H :: T] =
    RowEncoder.from { case h :: t =>
      CellEncoder[H].encode(h) +: RowEncoder[T].encode(t)
    }

  implicit def hlistCellEncoder[H: CellEncoder]: CellEncoder[H :: HNil] =
    CellEncoder[H].contramap { case (h :: _) =>
      h
    }

  implicit val hnilRowEncoder: RowEncoder[HNil] = RowEncoder.from(_ => Seq.empty)
}
