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
package joda

import kantan.codecs.export.Exported
import kantan.codecs.strings.{StringDecoder, StringEncoder}
import kantan.codecs.strings.joda.time._
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}

/** Declares [[kantan.csv.CellDecoder]] and [[kantan.csv.CellEncoder]] instances for joda-time types.
  *
  * These instances are only available if an implicit `org.joda.time.format.DateTimeFormat` is in scope.
  *
  * Note that this is a convenience - the exact same effect can be achieved by importing
  * `kantan.codec.strings.joda.time._`. The sole purpose of this is to keep things simple for users that don't want or
  * need to learn about kantan.csv's internals.
  */
package object time extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] {
  override def decoderFrom[D](d: StringDecoder[D]): CellDecoder[D] = codecs.fromStringDecoder(d)
  override def encoderFrom[D](e: StringEncoder[D]): CellEncoder[D] = codecs.fromStringEncoder(e)

  implicit val defaultDateTimeCellDecoder: Exported[CellDecoder[DateTime]] =
    Exported(defaultDateTimeDecoder)
  implicit val defaultLocalDateTimeCellDecoder: Exported[CellDecoder[LocalDateTime]] =
    Exported(defaultLocalDateTimeDecoder)
  implicit val defaultLocalDateCellDecoder: Exported[CellDecoder[LocalDate]] =
    Exported(defaultLocalDateDecoder)
  implicit val defaultLocalTimeCellDecoder: Exported[CellDecoder[LocalTime]] =
    Exported(defaultLocalTimeDecoder)

  implicit val defaultDateTimeCellEncoder: Exported[CellEncoder[DateTime]] =
    Exported(defaultDateTimeEncoder)
  implicit val defaultLocalDateTimeCellEncoder: Exported[CellEncoder[LocalDateTime]] =
    Exported(defaultLocalDateTimeEncoder)
  implicit val defaultLocalDateCellEncoder: Exported[CellEncoder[LocalDate]] =
    Exported(defaultLocalDateEncoder)
  implicit val defaultLocalTimeCellEncoder: Exported[CellEncoder[LocalTime]] =
    Exported(defaultLocalTimeEncoder)
}
