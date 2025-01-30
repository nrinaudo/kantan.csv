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

import kantan.codecs.resource.ResourceIterator
import kantan.csv.engine.ReaderEngine

import java.io.Reader

/** Provides instance creation and summoning methods. */
object CsvReader {
  @deprecated("use apply(Reader, CsvConfiguration) instead", "0.1.18")
  def apply[A: HeaderDecoder](reader: Reader, sep: Char, header: Boolean)(implicit
    e: ReaderEngine
  ): CsvReader[ReadResult[A]] =
    CsvReader(reader, rfc.withCellSeparator(sep).withHeader(header))

  /** Opens a [[CsvReader]] on the specified `Reader`. */
  def apply[A: HeaderDecoder](reader: Reader, conf: CsvConfiguration)(implicit
    e: ReaderEngine
  ): CsvReader[ReadResult[A]] = {
    val data: CsvReader[ReadResult[Seq[String]]] = e.readerFor(reader, conf)

    val decoder =
      if(conf.hasHeader && data.hasNext)
        data.next().flatMap(header => HeaderDecoder[A].fromHeader(header.map(_.trim())))
      else Right(HeaderDecoder[A].noHeader)

    decoder
      .map(d => data.map(_.flatMap(d.decode)))
      .left
      .map(error => ResourceIterator(ReadResult.failure(error)))
      .merge
  }
}
