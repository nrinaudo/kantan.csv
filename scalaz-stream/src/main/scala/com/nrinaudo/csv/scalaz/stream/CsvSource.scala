package com.nrinaudo.csv.scalaz.stream

import com.nrinaudo.csv.{CsvInput, RowReader}
import simulacrum.typeclass

import scala.io.Source
import scalaz.concurrent.Task
import scalaz.stream._

@typeclass trait CsvSource[S] {
  def source(s: S): Source

  // TODO: rename
  def src[A: RowReader](s: S, sep: Char, header: Boolean): Process[Task, A] = {
    io.resource(Task.delay(source(s)))(src => Task.delay(src.close())) { src =>
      lazy val lines = CsvInput[Source].rows(src, sep, header)
      Task.delay { if(lines.hasNext) lines.next() else throw Cause.Terminated(Cause.End) }
    }
  }
}

object CsvSource {
  implicit def fromInput[S: CsvInput]: CsvSource[S] = new CsvSource[S] {
    override def source(s: S): Source = CsvInput[S].source(s)
  }
}
