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
  def defaultFormat(sep: Char): CSVFormat = CSVFormat.RFC4180.withDelimiter(sep)

  def readerFrom(f: Char ⇒ CSVFormat): ReaderEngine = ReaderEngine { (r, s) ⇒
    ResourceIterator.fromIterator(f(s).parse(r).iterator.asScala.map(CsvSeq.apply))
  }

  implicit val reader: ReaderEngine = readerFrom(defaultFormat)

  def writerFrom(f: Char ⇒ CSVFormat): WriterEngine = WriterEngine { (w, s) ⇒
    CsvWriter(new CSVPrinter(w, f(s).withQuoteMode(QuoteMode.MINIMAL))) { (csv, ss) ⇒
      csv.printRecord(ss.asJava)
    }(_.close())
  }

  implicit val writer: WriterEngine = writerFrom(defaultFormat)
}
