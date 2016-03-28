package kantan.csv.engine

import java.io.{Reader, Writer}

import kantan.csv._
import org.apache.commons.csv.{CSVFormat, CSVPrinter, QuoteMode}

import scala.collection.JavaConverters._

package object commons {
  implicit val reader = ReaderEngine  { (reader: Reader, separator: Char) ⇒
    CsvReader.fromUnsafe(CSVFormat.RFC4180.withDelimiter(separator).parse(reader))(_.iterator.asScala.map(CsvSeq.apply))(_.close())
  }

  implicit val writer = WriterEngine { (writer: Writer, separator: Char) ⇒
    CsvWriter(new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(separator).withQuoteMode(QuoteMode.MINIMAL))) { (csv, ss) ⇒
      csv.printRecord(ss.asJava)
    }(_.close())
  }
}
