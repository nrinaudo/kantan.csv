package kantan.csv.engine

import java.io.{Reader, Writer}

import kantan.csv._

package object jackson {
  implicit val reader = ReaderEngine { (reader: Reader, separator: Char) ⇒
    CsvReader.fromUnsafe(JacksonCsv.parse(reader, separator))(it ⇒ it)(_.close())
  }

  implicit val writer = WriterEngine { (writer: Writer, separator: Char) ⇒
    CsvWriter(JacksonCsv.write(writer, separator)) { (out, ss) ⇒
      out.write(ss.toArray)
      ()
    }(_.close())
  }
}
