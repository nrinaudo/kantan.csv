package kantan.csv.engine

import java.io.{Reader, Writer}

import kantan.csv.{ParseResult, _}
import org.apache.commons.csv.{CSVFormat, CSVPrinter, CSVRecord, QuoteMode}

package object commons {
  implicit val reader = ReaderEngine  { (reader: Reader, separator: Char) ⇒
    val parser = CSVFormat.RFC4180.withDelimiter(separator).parse(reader)
    val csv = parser.iterator()

    new CsvReader[CsvResult[Seq[String]]] {
      override def hasNext = csv.hasNext
      override protected def readNext() =
        if(hasNext) ParseResult(new CsvSeq(csv.next()))
        else        throw new NoSuchElementException
      override def close() = parser.close()
    }
  }

  implicit val writer = WriterEngine { (writer: Writer, separator: Char) ⇒
    import scala.collection.JavaConverters._

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
