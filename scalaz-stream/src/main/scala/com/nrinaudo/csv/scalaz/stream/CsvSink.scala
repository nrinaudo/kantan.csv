package com.nrinaudo.csv.scalaz.stream

import java.io.PrintWriter

import com.nrinaudo.csv.{CsvOutput, RowEncoder}
import simulacrum.{op, noop, typeclass}

import scalaz.concurrent.Task
import scalaz.stream._

@typeclass trait CsvSink[S] {
  @noop def toPrintWriter(s: S): PrintWriter

  @op("asCsvSink") def sink[A: RowEncoder](s: S, sep: Char, header: Seq[String]): Sink[Task, A] =
    io.resource(Task.delay(CsvOutput[PrintWriter].writer(toPrintWriter(s), sep, header)))(out => Task.delay(out.close()))(
      out => Task.now((a: A) => Task.delay { out.write(a); () })
    )
}

object CsvSink {
  implicit def fromOutput[S: CsvOutput]: CsvSink[S] = new CsvSink[S] {
    override def toPrintWriter(s: S): PrintWriter = CsvOutput[S].toPrintWriter(s)
  }
}
