package kantan.csv.engine

import java.io.{Reader, Writer}
import kantan.csv._
import org.apache.commons.csv.{CSVFormat, CSVPrinter, QuoteMode}
import scala.collection.JavaConverters._

package object commons {
  def format(sep: Char): CSVFormat = CSVFormat.RFC4180.withDelimiter(sep)

  implicit val reader = ReaderEngine  { (reader: Reader, sep: Char) ⇒
    CsvReader.fromUnsafe(format(sep).parse(reader))(_.iterator.asScala.map(CsvSeq.apply))(_.close())
  }

  implicit val writer = WriterEngine { (writer: Writer, sep: Char) ⇒
    CsvWriter(new CSVPrinter(writer, format(sep).withQuoteMode(QuoteMode.MINIMAL))) { (csv, ss) ⇒
      csv.printRecord(ss.asJava)
    }(_.close())
  }
}
