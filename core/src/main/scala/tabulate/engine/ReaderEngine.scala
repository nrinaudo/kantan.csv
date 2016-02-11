package tabulate.engine

import java.io.Reader

import tabulate.{CsvReader, DecodeResult}

trait ReaderEngine {
  def readerFor(reader: Reader, separator: Char): CsvReader[DecodeResult[Seq[String]]]
}

object ReaderEngine {
  def apply(f: (Reader, Char) ⇒ CsvReader[DecodeResult[Seq[String]]]): ReaderEngine = new ReaderEngine {
    override def readerFor(reader: Reader, separator: Char): CsvReader[DecodeResult[Seq[String]]] = f(reader, separator)
  }

  implicit val internal: ReaderEngine = ReaderEngine((reader, sep) ⇒ new InternalReader(reader, sep))
}
