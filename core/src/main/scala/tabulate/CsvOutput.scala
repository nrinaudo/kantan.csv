package tabulate

import java.io._

import simulacrum.{noop, op, typeclass}

import scala.io.Codec

@typeclass trait CsvOutput[S] { self =>
  @noop def writer(s: S): Writer

  @op("asCsvWriter")
  def csvWriter[A](s: S, separator: Char, header: Seq[String] = Seq.empty)(implicit ea: RowEncoder[A]): CsvWriter[A] = {
    if(header.isEmpty) new CsvWriter[A](writer(s), separator, ea.encode)
    else {
      val w = new CsvWriter(writer(s), separator, identity[Seq[String]])
      w.write(header)
      w.contramap(ea.encode)
    }
  }

  @noop
  def contramap[T](f: T => S): CsvOutput[T] = CsvOutput(t => self.writer(f(t)))

  @op("writeCsv")
  def write[A: RowEncoder](out: S, rows: Traversable[A], sep: Char, header: Seq[String] = Seq.empty): S = {
    rows.foldLeft(csvWriter(out, sep, header))(_ write _).close()
    out
  }
}

@export.imports[CsvOutput]
trait LowPriorityCsvOutputs

object CsvOutput extends LowPriorityCsvOutputs {
  def apply[S](f: S => Writer): CsvOutput[S] = new CsvOutput[S] {
    override def writer(s: S) = f(s)
  }

  implicit def outputStream[O <: OutputStream](implicit codec: Codec): CsvOutput[O] =
    writer.contramap(o => new OutputStreamWriter(o, codec.charSet))

  implicit def file(implicit codec: Codec): CsvOutput[File] = outputStream.contramap(f => new FileOutputStream(f))

  implicit def writer[W <: Writer]: CsvOutput[W] = CsvOutput(w => w)

}