package kantan.csv.engine

import java.io.Reader

import kantan.csv.{CsvReader, ReadResult}

trait ReaderEngine {
  def readerFor(reader: Reader, separator: Char): CsvReader[ReadResult[Seq[String]]]
}

object ReaderEngine {
  def apply(f: (Reader, Char) ⇒ CsvReader[ReadResult[Seq[String]]]): ReaderEngine = new ReaderEngine {
    override def readerFor(reader: Reader, separator: Char): CsvReader[ReadResult[Seq[String]]] = f(reader, separator)
  }

  implicit val internal: ReaderEngine = ReaderEngine((reader, sep) ⇒ new InternalReader(reader, sep))
}
