package com.nrinaudo.csv.scalaz.stream

import java.io.PrintWriter

import com.nrinaudo.csv.{CsvOutput, RowWriter}
import simulacrum.typeclass

import scalaz.concurrent.Task
import scalaz.stream._

@typeclass trait CsvSink[S] {
  def printWriter(s: S): PrintWriter

  // TODO: rename
  def rowsW[A: RowWriter](s: S, sep: Char, header: Seq[String]): Sink[Task, A] =
    io.resource(Task.delay(CsvOutput[PrintWriter].sink(printWriter(s), sep, header)))(out => Task.delay(out.close()))(
      out => Task.now((a: A) => Task.delay { out.write(a); () })
    )
}

object CsvSink {
  implicit def fromOutput[S: CsvOutput]: CsvSink[S] = new CsvSink[S] {
    override def printWriter(s: S): PrintWriter = CsvOutput[S].printWriter(s)
  }
}
