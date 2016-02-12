package kantan.csv.engine.jackson

import java.io.{Reader, Writer}

import kantan.csv
import kantan.csv.engine.{WriterEngine, ReaderEngine}
import kantan.csv.{CsvReader, CsvWriter}

class JacksonEngine extends ReaderEngine with WriterEngine {
  override def readerFor(reader: Reader, separator: Char) = {
    val iterator = JacksonCsv.parse(reader, separator)
    new CsvReader[csv.DecodeResult[Seq[String]]] {
      override protected def readNext() =
        if(hasNext) csv.DecodeResult(iterator.next())
        else        throw new NoSuchElementException
      override def hasNext = iterator.hasNext
      override def close() = iterator.close()
    }
  }

  override def writerFor(writer: Writer, separator: Char) = {
    val out = JacksonCsv.write(writer, separator)
    new CsvWriter[Seq[String]] {
      override def write(a: Seq[String]): CsvWriter[Seq[String]] = {
        out.write(a.toArray)
        this
      }
      override def close() = out.close()
    }
  }
}
