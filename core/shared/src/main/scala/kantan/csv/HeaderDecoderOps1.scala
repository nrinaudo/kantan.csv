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

import kantan.csv.HeaderDecoderOps0.EnrichedHeaderDecoder0
import kantan.csv.HeaderDecoderOps1.EnrichedHeaderDecoder1

trait HeaderDecoderOps0 extends HeaderDecoderOps1 {
  implicit def enrichedHeaderDecoder0[A](
    decoder: HeaderDecoder[Option[A]]
  ): EnrichedHeaderDecoder0[A] =
    new EnrichedHeaderDecoder0[A](decoder)
}

object HeaderDecoderOps0 {
  class EnrichedHeaderDecoder0[A](val decoder: HeaderDecoder[Option[A]]) extends AnyVal {
    def optional: HeaderDecoder[Option[A]] =
      new HeaderDecoder[Option[A]] {
        override def fromHeader(header: Seq[String]): DecodeResult[RowDecoder[Option[A]]] =
          decoder
            .fromHeader(header)
            .fold(
              _ => DecodeResult.success(RowDecoder.from(_ => Right(None))),
              rowDecoder => DecodeResult.success(rowDecoder)
            )

        override def noHeader: RowDecoder[Option[A]] =
          decoder.noHeader
      }
  }
}

trait HeaderDecoderOps1 {
  implicit def enrichedHeaderDecoder1[A](
    decoder: HeaderDecoder[A]
  ): EnrichedHeaderDecoder1[A] =
    new EnrichedHeaderDecoder1[A](decoder)

}

object HeaderDecoderOps1 {
  class EnrichedHeaderDecoder1[A](val decoder: HeaderDecoder[A]) extends AnyVal {
    def optional: HeaderDecoder[Option[A]] =
      new HeaderDecoder[Option[A]] {
        override def fromHeader(header: Seq[String]): DecodeResult[RowDecoder[Option[A]]] =
          decoder
            .fromHeader(header)
            .fold(
              _ => DecodeResult.success(RowDecoder.from(_ => Right(None))),
              rowDecoder => DecodeResult.success(rowDecoder.map(Some(_)))
            )

        override def noHeader: RowDecoder[Option[A]] =
          decoder.noHeader.map(Some(_))
      }
  }
}
