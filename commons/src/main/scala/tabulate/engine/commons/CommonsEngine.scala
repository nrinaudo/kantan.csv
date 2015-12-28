package tabulate.engine.commons

import java.io.{Reader, Writer}

import org.apache.commons.csv.{CSVFormat, CSVPrinter, CSVRecord}
import tabulate.engine.{ReaderEngine, WriterEngine}
import tabulate.{CsvReader, CsvWriter, DecodeResult}

private class CsvSeq(rec: CSVRecord) extends IndexedSeq[String] {
  override def length: Int = rec.size()
  override def apply(idx: Int): String = rec.get(idx)
}

class CommonsEngine extends ReaderEngine with WriterEngine {
  private def formatFor(sep: Char): CSVFormat = CSVFormat.RFC4180.withDelimiter(sep)

  override def readerFor(reader: Reader, separator: Char) = {
    val parser = formatFor(separator).parse(reader)
    val csv = parser.iterator()

    new CsvReader[DecodeResult[Seq[String]]] {
      override def hasNext = csv.hasNext
      override protected def readNext() = DecodeResult(new CsvSeq(csv.next()))
      override def close() = parser.close()
    }
  }

  override def writerFor(writer: Writer, separator: Char) = {
    val csv = new CSVPrinter(writer, formatFor(separator))
    new CsvWriter[Seq[String]] {
      override def write(ss: Seq[String]) = {
        csv.printRecords(ss)
        this
      }
      override def close() = csv.close()
    }
  }
}
