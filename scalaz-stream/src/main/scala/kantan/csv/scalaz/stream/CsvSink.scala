package kantan.csv.scalaz.stream

import java.io.Writer

import kantan.csv
import kantan.csv.engine.WriterEngine
import kantan.csv.{CsvOutput, CsvWriter}
import simulacrum.{noop, op, typeclass}

import scalaz.concurrent.Task
import scalaz.stream._

/** Turns instances of `S` into CSV sinks.
  *
  * Any type `S` that has a implicit instance of [[CsvSink]] in scope will be enriched by the `asCsvSink` method (which
  * maps to [[sink]]).
  *
  * Additionally, any type that has an instance of `CsvOutput` in scope automatically gets an instance of [[CsvSink]].
  */
@typeclass trait CsvSink[S] extends Serializable {
  @noop def writer(s: S): Writer

  @op("asCsvSink") def sink[A: csv.RowEncoder](s: S, sep: Char, header: Seq[String] = Seq.empty)(implicit engine: WriterEngine): Sink[Task, A] =
    CsvSink[A](writer(s), sep, header)
}

object CsvSink {
  def apply[A](writer: ⇒ CsvWriter[A]): Sink[Task, A] = io.resource(Task.delay(writer))(out ⇒ Task.delay(out.close()))(
    out ⇒ Task.now((a: A) ⇒ Task.delay { out.write(a); () })
  )
  def apply[A: csv.RowEncoder](writer: ⇒ Writer, sep: Char, header: Seq[String] = Seq.empty)(implicit engine: WriterEngine): Sink[Task, A] =
    CsvSink(CsvWriter[A](writer, sep, header))

  implicit def fromOutput[S](implicit os: CsvOutput[S]): CsvSink[S] = new CsvSink[S] {
    override def writer(s: S) = os.open(s)
  }
}
