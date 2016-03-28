package kantan.csv.engine

import java.io.{Reader, Writer}

import com.opencsv.{CSVReader, CSVWriter}
import kantan.csv._

// TODO: known bugs
// - \r\n is not preserved within quoted cells, it's turned into \n (csv spectrum test)
// - unescaped double quotes are not supported.

package object opencsv {
  // Note that using the `null` character as escape is a bit of a cheat, but it kind of works, mostly. Escaping is not
  // part of the CSV format, but I found no other way to disable it.
  implicit val reader = ReaderEngine { (reader: Reader, separator: Char) ⇒
    CsvReader.fromUnsafe(new CSVReader(reader, separator, '"', '\u0000', 0, false, false, false))(_.iterator())(_.close())
  }

  implicit val writer = WriterEngine { (writer: Writer, separator: Char) ⇒
    CsvWriter(new CSVWriter(writer, separator, '"', "\r\n"))((out, ss) ⇒ out.writeNext(ss.toArray))(_.close())
  }
}
