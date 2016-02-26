package kantan.csv.engine.opencsv

import java.io.{Reader, Writer}

import com.opencsv._
import kantan.csv._
import kantan.csv.engine.{WriterEngine, ReaderEngine}

import scala.collection.mutable

// TODO: known bugs
// - \r\n is not preserved within quoted cells, it's turned into \n (csv spectrum test)
// - unescaped double quotes are not supported.

class OpenCsvEngine extends ReaderEngine with WriterEngine {
  override def readerFor(reader: Reader, separator: Char) = {
    // Note that using the `null` character as escape is a bit of a cheat, but it kind of works, mostly. Escaping is not
    // part of the CSV format, but I found no other way to disable it.
    val csv = new CSVReader(reader, separator, '"', '\u0000', 0, false, false, false)

    new CsvReader[CsvResult[Seq[String]]] {
      var n: Array[String] = csv.readNext()
      override def hasNext = n != null
      override protected def readNext() = {
        val buffer = ParseResult.success(mutable.WrappedArray.make(n))
        n = csv.readNext()
        buffer
      }
      override def close() = csv.close()
    }
  }

  override def writerFor(writer: Writer, separator: Char) = {
    val out = new CSVWriter(writer, separator, '"', "\r\n")
    new CsvWriter[Seq[String]] {
      override def write(ss: Seq[String]) = {
        out.writeNext(ss.toArray)
        this
      }
      override def close() = out.close()
    }
  }
}
