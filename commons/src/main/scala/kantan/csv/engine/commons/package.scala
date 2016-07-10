/*
 * Copyright 2016 Nicolas Rinaudo
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

import java.io.{Reader, Writer}
import kantan.codecs.ResourceIterator
import kantan.csv._
import org.apache.commons.csv.{CSVFormat, CSVPrinter, QuoteMode}
import scala.collection.JavaConverters._

package object commons {
  def format(sep: Char): CSVFormat = CSVFormat.RFC4180.withDelimiter(sep)

  implicit val reader = ReaderEngine { (reader: Reader, sep: Char) ⇒
    ResourceIterator.fromIterator(format(sep).parse(reader).iterator.asScala.map(CsvSeq.apply))
  }

  implicit val writer = WriterEngine { (writer: Writer, sep: Char) ⇒
    CsvWriter(new CSVPrinter(writer, format(sep).withQuoteMode(QuoteMode.MINIMAL))) { (csv, ss) ⇒
      csv.printRecord(ss.asJava)
    }(_.close())
  }
}
