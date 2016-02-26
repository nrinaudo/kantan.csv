package kantan.csv.engine.commons

import java.io.{Reader, Writer}

import kantan.csv._
import kantan.csv.engine.{WriterEngine, ReaderEngine}
import org.apache.commons.csv.{CSVFormat, CSVPrinter, CSVRecord, QuoteMode}

private class CsvSeq(rec: CSVRecord) extends IndexedSeq[String] {
  override def length: Int = rec.size()
  override def apply(idx: Int): String = rec.get(idx)
}

class CommonsEngine extends ReaderEngine with WriterEngine {
  override def readerFor(reader: Reader, separator: Char) = {
    val parser = CSVFormat.RFC4180.withDelimiter(separator).parse(reader)
    val csv = parser.iterator()

    new CsvReader[CsvResult[Seq[String]]] {
      override def hasNext = csv.hasNext
      override protected def readNext() =
        if(hasNext) ParseResult.success(new CsvSeq(csv.next()))
        else        throw new NoSuchElementException
      override def close() = parser.close()
    }
  }

  override def writerFor(writer: Writer, separator: Char) = {
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
