package tabulate.interop.scalaz.stream

import java.io.Writer

import simulacrum.{op, noop, typeclass}
import tabulate.engine.WriterEngine
import tabulate.{CsvWriter, CsvOutput, RowEncoder}

import scalaz.Alpha.S
import scalaz.Sink
import scalaz.concurrent.Task
import scalaz.stream._

/** Turns instances of `S` into CSV sinks.
  *
  * Any type `S` that has a implicit instance of `CsvSink` in scope will be enriched by the `asCsvSink` method (which
  * maps to [[sink]]).
  *
  * Additionally, any type that has an instance of `CsvOutput` in scope automatically gets an instance of `CsvSink`.
  */
@typeclass trait CsvSink[S] {
  @noop def writer(s: S): Writer

  @op("asCsvSink") def sink[A: RowEncoder](s: S, sep: Char, header: Seq[String] = Seq.empty)(implicit engine: WriterEngine): Sink[Task, A] =
    CsvSink[A](writer(s), sep, header)
}

object CsvSink {
  def apply[A](writer: => CsvWriter[A]): Sink[Task, A] = io.resource(Task.delay(writer))(out => Task.delay(out.close()))(
    out => Task.now((a: A) => Task.delay { out.write(a); () })
  )
  def apply[A: RowEncoder](writer: => Writer, sep: Char, header: Seq[String] = Seq.empty)(implicit engine: WriterEngine): Sink[Task, A] =
    CsvSink(CsvWriter[A](writer, sep, header))

  implicit def fromOutput[S](implicit os: CsvOutput[S]): CsvSink[S] = new CsvSink[S] {
    override def writer(s: S) = os.writer(s)
  }
}
