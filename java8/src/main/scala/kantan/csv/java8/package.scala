/*
 * Copyright 2017 Nicolas Rinaudo
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

import java.time._
import kantan.codecs.export.Exported
import kantan.codecs.strings._
import kantan.codecs.strings.java8.TimeCodecCompanion

package object java8 extends TimeCodecCompanion[String, DecodeError, codecs.type] {
  override def decoderFrom[D](d: StringDecoder[D]): CellDecoder[D] = codecs.fromStringDecoder(d)
  override def encoderFrom[D](e: StringEncoder[D]): CellEncoder[D] = codecs.fromStringEncoder(e)

  implicit val defaultInstantCellDecoder: Exported[CellDecoder[Instant]] =
    Exported(defaultInstantDecoder)
  implicit val defaultZonedDateTimeCellDecoder: Exported[CellDecoder[ZonedDateTime]] =
    Exported(defaultZonedDateTimeDecoder)
  implicit val defaultOffsetDateTimeCellDecoder: Exported[CellDecoder[OffsetDateTime]] =
    Exported(defaultOffsetDateTimeDecoder)
  implicit val defaultLocalDateTimeCellDecoder: Exported[CellDecoder[LocalDateTime]] =
    Exported(defaultLocalDateTimeDecoder)
  implicit val defaultLocalDateCellDecoder: Exported[CellDecoder[LocalDate]] =
    Exported(defaultLocalDateDecoder)
  implicit val defaultLocalTimeCellDecoder: Exported[CellDecoder[LocalTime]] =
    Exported(defaultLocalTimeDecoder)

  implicit val defaultInstantCellEncoder: Exported[CellEncoder[Instant]] =
    Exported(defaultInstantEncoder)
  implicit val defaultZonedDateTimeCellEncoder: Exported[CellEncoder[ZonedDateTime]] =
    Exported(defaultZonedDateTimeEncoder)
  implicit val defaultOffsetDateTimeCellEncoder: Exported[CellEncoder[OffsetDateTime]] =
    Exported(defaultOffsetDateTimeEncoder)
  implicit val defaultLocalDateTimeCellEncoder: Exported[CellEncoder[LocalDateTime]] =
    Exported(defaultLocalDateTimeEncoder)
  implicit val defaultLocalDateCellEncoder: Exported[CellEncoder[LocalDate]] =
    Exported(defaultLocalDateEncoder)
  implicit val defaultLocalTimeCellEncoder: Exported[CellEncoder[LocalTime]] =
    Exported(defaultLocalTimeEncoder)
}
