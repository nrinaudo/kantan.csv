package tabulate.engine.opencsv

import java.io.{Reader, Writer}

import com.opencsv._
import tabulate.engine.{ReaderEngine, WriterEngine}
import tabulate.{CsvReader, CsvWriter, DecodeResult}

import scala.collection.mutable

class OpenCsvEngine extends ReaderEngine with WriterEngine {
  override def readerFor(reader: Reader, separator: Char) = {
    val csv = new CSVReader(reader, separator, '"', '\u0000', 0, false, false, false)

    new CsvReader[DecodeResult[Seq[String]]] {
      var n: Array[String] = csv.readNext()
      override def hasNext = n != null
      override protected def readNext() = {
        val buffer = DecodeResult(mutable.WrappedArray.make(n))
        n = csv.readNext()
        buffer
      }
      override def close() = csv.close()
    }
  }
  override def writerFor(writer: Writer, separator: Char) = {
    val out = new CSVWriter(writer, separator)
    new CsvWriter[Seq[String]] {
      override def write(ss: Seq[String]) = {
        out.writeNext(ss.toArray)
        this
      }
      override def close() = out.close()
    }
  }
}
