package tabulate.engine.commons

import java.io.{Reader, Writer}
import org.apache.commons.csv.{CSVPrinter, CSVFormat}

import scala.collection.JavaConverters._

import tabulate.engine.{ReaderEngine, WriterEngine}
import tabulate.{CsvReader, CsvWriter, DecodeResult}

class CommonsEngine extends ReaderEngine with WriterEngine {
  private def formatFor(sep: Char): CSVFormat = CSVFormat.RFC4180.withDelimiter(sep)

  override def readerFor(reader: Reader, separator: Char) = {
    val csv = formatFor(separator).parse(reader).iterator()

    new CsvReader[DecodeResult[Seq[String]]] {
      override def hasNext = csv.hasNext
      override protected def readNext() = DecodeResult(csv.next().asScala.toSeq)
      override def close() = reader.close()
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
