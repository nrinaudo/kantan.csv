package kantan.csv.scalaz.stream

import _root_.scalaz.concurrent.Task
import _root_.scalaz.stream.{Process, Sink}
import kantan.csv._
import kantan.csv.engine.{ReaderEngine, WriterEngine}

object ops {
  implicit class CsvSinkOps[A](val a: A) extends AnyVal {
    def asCsvSink[B: RowEncoder](sep: Char, header: Seq[String] = Seq.empty)(implicit oa: CsvSink[A], e: WriterEngine): Sink[Task, B] =
      oa.sink(a, sep, header)

  }

  implicit class CsvSourceOps[A](val a: A) extends AnyVal {
    def asCsvSource[B: RowDecoder](sep: Char, header: Boolean)(implicit ai: CsvSource[A], e: ReaderEngine): Process[Task, CsvResult[B]] =
      ai.source(a, sep, header)

    def asUnsafeCsvSource[B: RowDecoder](sep: Char, header: Boolean)(implicit ai: CsvSource[A], e: ReaderEngine): Process[Task, B] =
      ai.unsafeSource[B](a, sep, header)
  }
}
