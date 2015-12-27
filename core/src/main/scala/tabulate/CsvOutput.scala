package tabulate

import java.io._

import simulacrum.{noop, op, typeclass}
import tabulate.engine.WriterEngine

import scala.io.Codec

@typeclass trait CsvOutput[S] { self =>
  @noop def writer(s: S): Writer

  @op("asCsvWriter")
  def csvWriter[A](s: S, separator: Char, header: Seq[String] = Seq.empty)(implicit ea: RowEncoder[A], engine: WriterEngine): CsvWriter[A] =
    CsvWriter(writer(s), separator, header)

  @noop
  def contramap[T](f: T => S): CsvOutput[T] = CsvOutput.fromWriter(t => self.writer(f(t)))

  @op("writeCsv")
  def write[A: RowEncoder](out: S, rows: Traversable[A], sep: Char, header: Seq[String] = Seq.empty): S = {
    csvWriter(out, sep, header).write(rows).close()
    out
  }
}

@export.imports[CsvOutput]
trait LowPriorityCsvOutputs

object CsvOutput extends LowPriorityCsvOutputs {
  def fromWriter[S](f: S => Writer): CsvOutput[S] = new CsvOutput[S] {
    override def writer(s: S) = f(s)
  }

  def fromStream[S](f: S => OutputStream)(implicit codec: Codec): CsvOutput[S] = new CsvOutput[S] {
    override def writer(s: S) = new OutputStreamWriter(f(s), codec.charSet)
  }

  implicit def file(implicit codec: Codec): CsvOutput[File] = fromStream(f => new FileOutputStream(f))
}