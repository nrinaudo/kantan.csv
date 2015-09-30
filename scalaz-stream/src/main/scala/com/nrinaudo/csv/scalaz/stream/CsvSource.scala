package com.nrinaudo.csv.scalaz.stream

import com.nrinaudo.csv.{CsvInput, RowReader}
import simulacrum.{op, noop, typeclass}

import scala.io.Source
import scalaz.concurrent.Task
import scalaz.stream._

@typeclass trait CsvSource[S] {
  @noop def toSource(s: S): Source

  @op("asCsvSource") def source[A: RowReader](s: S, sep: Char, header: Boolean): Process[Task, A] = {
    io.resource(Task.delay(toSource(s)))(src => Task.delay(src.close())) { src =>
      lazy val lines = CsvInput[Source].rows(src, sep, header)
      Task.delay { if(lines.hasNext) lines.next() else throw Cause.Terminated(Cause.End) }
    }
  }
}

object CsvSource {
  implicit def fromInput[S: CsvInput]: CsvSource[S] = new CsvSource[S] {
    override def toSource(s: S): Source = CsvInput[S].toSource(s)
  }
}
