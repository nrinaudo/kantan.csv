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

import kantan.codecs.laws.CodecLaws
import kantan.codecs.laws.CodecValue
import kantan.codecs.laws.DecoderLaws
import kantan.codecs.laws.EncoderLaws
import kantan.csv.ops._

package object laws {
  type CellDecoderLaws[A] = DecoderLaws[String, A, DecodeError, codecs.type]
  type CellEncoderLaws[A] = EncoderLaws[String, A, codecs.type]
  type CellCodecLaws[A]   = CodecLaws[String, A, DecodeError, codecs.type]
  type RowDecoderLaws[A]  = DecoderLaws[Seq[String], A, DecodeError, codecs.type]
  type RowEncoderLaws[A]  = EncoderLaws[Seq[String], A, codecs.type]
  type RowCodecLaws[A]    = CodecLaws[Seq[String], A, DecodeError, codecs.type]

  type CellValue[A]   = CodecValue[String, A, codecs.type]
  type LegalCell[A]   = CodecValue.LegalValue[String, A, codecs.type]
  type IllegalCell[A] = CodecValue.IllegalValue[String, A, codecs.type]
  type RowValue[A]    = CodecValue[Seq[String], A, codecs.type]
  type LegalRow[A]    = CodecValue.LegalValue[Seq[String], A, codecs.type]
  type IllegalRow[A]  = CodecValue.IllegalValue[Seq[String], A, codecs.type]

  def asCsv[A](data: List[RowValue[A]], conf: CsvConfiguration): String =
    data.map(_.encoded).asCsv(conf)
}
