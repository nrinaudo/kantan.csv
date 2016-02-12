package kantan.csv.engine

import java.io.Writer

import kantan.csv.CsvWriter

trait WriterEngine {
  def writerFor(writer: Writer, separator: Char): CsvWriter[Seq[String]]
}

object WriterEngine {
  def apply(f: (Writer, Char) ⇒ CsvWriter[Seq[String]]): WriterEngine = new WriterEngine {
    override def writerFor(writer: Writer, separator: Char): CsvWriter[Seq[String]] = f(writer, separator)
  }
  implicit val internal: WriterEngine = WriterEngine((writer, sep) ⇒ new InternalWriter(writer, sep))
}

