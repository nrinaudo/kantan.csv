package kantan.csv.engine

import java.io.{Reader, Writer}

import kantan.csv._
import org.apache.commons.csv.{CSVFormat, CSVPrinter, QuoteMode}

import scala.collection.JavaConverters._

package object commons {
  implicit val reader = ReaderEngine  { (reader: Reader, separator: Char) ⇒
    val parser = CSVFormat.RFC4180.withDelimiter(separator).parse(reader)
    val csv = parser.iterator()

    CsvReader.fromUnsafe(csv.asScala.map(CsvSeq.apply))(() ⇒ parser.close())
  }

  implicit val writer = WriterEngine { (writer: Writer, separator: Char) ⇒
    val csv = new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(separator).withQuoteMode(QuoteMode.MINIMAL))
    new CsvWriter[Seq[String]] {
      override def write(ss: Seq[String]) = {
        csv.printRecord(ss.asJava)
        this
      }
      override def close() = csv.close()
    }
  }
}
