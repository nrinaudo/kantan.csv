package kantan.csv.engine

import java.io.{Reader, Writer}

import kantan.csv.{ParseResult, _}

package object jackson {
  implicit val reader = ReaderEngine { (reader: Reader, separator: Char) ⇒
    val iterator = JacksonCsv.parse(reader, separator)
    new CsvReader[CsvResult[Seq[String]]] {
      override protected def readNext() =
        if(hasNext) ParseResult(iterator.next())
        else        throw new NoSuchElementException
      override def hasNext = iterator.hasNext
      override def close() = iterator.close()
    }
  }

  implicit val writer = WriterEngine {(writer: Writer, separator: Char) ⇒
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
