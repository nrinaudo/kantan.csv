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
import kantan.codecs.strings.joda.time.{JodaTimeCodecCompanion, ToFormatLiteral}
import org.joda.time.{DateTime, LocalDate, LocalDateTime, LocalTime}

/** Declares [[kantan.csv.CellDecoder]] and [[kantan.csv.CellEncoder]] instances for joda-time types.
  *
  * Note that the type for default codecs might come as a surprise: the wrapping `Exported` is used to lower their
  * priority. This is necessary because the standard use case will be to `import kantan.csv.joda.time._`, which
  * brings both the instance creation and default instances in scope. Without this type trickery, custom instances
  * and default ones would always clash.
  */
package object time extends JodaTimeCodecCompanion[String, DecodeError, codecs.type] with ToFormatLiteral {

  override def decoderFrom[D](d: StringDecoder[D]): CellDecoder[D] = codecs.fromStringDecoder(d)
  override def encoderFrom[D](e: StringEncoder[D]): CellEncoder[D] = codecs.fromStringEncoder(e)

  implicit val defaultDateTimeCellDecoder: Exported[CellDecoder[DateTime]] = Exported(defaultDateTimeDecoder)
  implicit val defaultLocalDateTimeCellDecoder: Exported[CellDecoder[LocalDateTime]] = Exported(
    defaultLocalDateTimeDecoder
  )
  implicit val defaultLocalTimeCellDecoder: Exported[CellDecoder[LocalTime]] = Exported(defaultLocalTimeDecoder)
  implicit val defaultLocalDateCellDecoder: Exported[CellDecoder[LocalDate]] = Exported(defaultLocalDateDecoder)

  implicit val defaultDateTimeCellEncoder: Exported[CellEncoder[DateTime]] = Exported(defaultDateTimeEncoder)
  implicit val defaultLocalDateTimeCellEncoder: Exported[CellEncoder[LocalDateTime]] = Exported(
    defaultLocalDateTimeEncoder
  )
  implicit val defaultLocalTimeCellEncoder: Exported[CellEncoder[LocalTime]] = Exported(defaultLocalTimeEncoder)
  implicit val defaultLocalDateCellEncoder: Exported[CellEncoder[LocalDate]] = Exported(defaultLocalDateEncoder)

}
