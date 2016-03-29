package kantan.csv.scalaz.stream

import java.io.Writer
import kantan.csv.{CsvOutput, CsvWriter, RowEncoder}
import kantan.csv.engine.WriterEngine
import scalaz.concurrent.Task
import scalaz.stream._

/** Turns instances of `S` into CSV sinks.
  *
  * Any type `S` that has a implicit instance of [[CsvSink]] in scope will be enriched by the `asCsvSink` method (which
  * maps to [[sink]]).
  *
  * Additionally, any type that has an instance of `CsvOutput` in scope automatically gets an instance of [[CsvSink]].
  */
trait CsvSink[S] extends Serializable {
  def writer(s: S): Writer

  def sink[A: RowEncoder](s: S, sep: Char, header: Seq[String] = Seq.empty)(implicit e: WriterEngine): Sink[Task, A] =
    CsvSink[A](writer(s), sep, header)
}

object CsvSink {
  def apply[A](implicit sa: CsvSink[A]): CsvSink[A] = sa

  def apply[A](writer: ⇒ CsvWriter[A]): Sink[Task, A] =
    io.resource(Task.delay(writer))(out ⇒ Task.delay(out.close())) { out ⇒
      Task.now((a: A) ⇒ Task.delay { out.write(a); () })
    }


  def apply[A: RowEncoder](writer: ⇒ Writer, sep: Char, header: Seq[String] = Seq.empty)
                          (implicit e: WriterEngine): Sink[Task, A] =
    CsvSink(CsvWriter[A](writer, sep, header))

  implicit def fromOutput[S](implicit os: CsvOutput[S]): CsvSink[S] = new CsvSink[S] {
    override def writer(s: S) = os.open(s)
  }
}
