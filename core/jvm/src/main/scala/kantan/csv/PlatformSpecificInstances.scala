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

import kantan.codecs.strings.StringDecoder
import kantan.codecs.strings.StringEncoder

import java.text.DateFormat
import java.util.Date

trait PlatformSpecificCellDecoderInstances {

  def dateDecoder(format: DateFormat): CellDecoder[Date] =
    codecs.fromStringDecoder(StringDecoder.dateDecoder(format))

}

trait PlatformSpecificCellEncoderInstances {

  def dateEncoder(format: DateFormat): CellEncoder[Date] =
    codecs.fromStringEncoder(StringEncoder.dateEncoder(format))

}

trait PlatformSpecificCellCodecInstances {

  def dateCodec(format: DateFormat): CellCodec[Date] =
    CellCodec.from(CellDecoder.dateDecoder(format), CellEncoder.dateEncoder(format))

}
