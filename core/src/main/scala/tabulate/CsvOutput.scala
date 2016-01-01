package tabulate

import java.io._

import simulacrum.{noop, op, typeclass}
import tabulate.engine.WriterEngine

import scala.io.Codec

@typeclass trait CsvOutput[S] { self =>
  @noop
  def open(s: S): Writer

  @op("asCsvWriter")
  def writer[A](s: S, separator: Char, header: Seq[String] = Seq.empty)(implicit ea: RowEncoder[A], engine: WriterEngine): CsvWriter[A] =
    CsvWriter(open(s), separator, header)

  @noop
  def contramap[T](f: T => S): CsvOutput[T] = CsvOutput(t => self.open(f(t)))

  @op("writeCsv")
  def write[A: RowEncoder](out: S, rows: Traversable[A], sep: Char, header: Seq[String] = Seq.empty): S = {
    writer(out, sep, header).write(rows).close()
    out
  }
}

@export.imports[CsvOutput]
trait LowPriorityCsvOutputs

object CsvOutput extends LowPriorityCsvOutputs {
  def apply[A](f: A => Writer): CsvOutput[A] = new CsvOutput[A] {
    override def open(s: A): Writer = f(s)
  }

  implicit def writer[W <: Writer]: CsvOutput[W] = CsvOutput(w => w)

  implicit def outputStream[O <: OutputStream](implicit codec: Codec): CsvOutput[O] = writer.contramap(o => new OutputStreamWriter(o, codec.charSet))
  implicit def file(implicit codec: Codec): CsvOutput[File] = outputStream.contramap(f => new FileOutputStream(f))
}