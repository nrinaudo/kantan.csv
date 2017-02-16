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

package kantan.csv.engine

import kantan.codecs.resource.ResourceIterator
import kantan.csv._
import org.apache.commons.csv.{CSVFormat, CSVPrinter, QuoteMode}
import scala.collection.JavaConverters._

package object commons {
  // - Formats ---------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Type of functions that create a `CSVFormat` instance from a given column separator. */
  type CSVFormatBuilder = Char ⇒ CSVFormat

  /** Creates a default `CSVFormat` instance using the specified column separator. */
  def defaultFormat(sep: Char): CSVFormat = CSVFormat.RFC4180.withDelimiter(sep)



  // - Reader engines --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Creates a new [[ReaderEngine]] from the specified function.
    *
    * The purpose of this is to let developers use some of the commons.csv features that kantan.csv does not expose
    * through its public API.
    */
  def readerEngineFrom(f: CSVFormatBuilder): ReaderEngine = ReaderEngine { (r, s) ⇒
    ResourceIterator.fromIterator(f(s).parse(r).iterator.asScala.map(CsvSeq.apply))
  }

  /** Default commons.csv [[ReaderEngine]].
    *
    * It's possible to tweak the behaviour of the underlying writer through [[readerEngineFrom]].
    */
  implicit val commonsCsvReaderEngine: ReaderEngine = readerEngineFrom(defaultFormat)



  // - Writer engines --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Creates a new [[WriterEngine]] from the specified [[CSVFormatBuilder]].
    *
    * The purpose of this is to let developers use some of the commons.csv features that kantan.csv does not expose
    * through its public API.
    */
  def writerEngineFrom(f: CSVFormatBuilder): WriterEngine = WriterEngine { (w, s) ⇒
    CsvWriter(new CSVPrinter(w, f(s).withQuoteMode(QuoteMode.MINIMAL))) { (csv, ss) ⇒
      csv.printRecord(ss.asJava)
    }(_.close())
  }

  /** Default commons.csv [[WriterEngine]].
    *
    * It's possible to tweak the behaviour of the underlying writer through [[writerEngineFrom]].
    */
  implicit val commonsCsvWriterEngine: WriterEngine = writerEngineFrom(defaultFormat)
}
