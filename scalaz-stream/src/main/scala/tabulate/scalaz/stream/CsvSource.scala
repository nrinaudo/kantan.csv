package tabulate.scalaz.stream

import simulacrum.{op, noop, typeclass}
import tabulate.{CsvInput, DecodeResult, RowDecoder}

import scala.io.Source
import scalaz.concurrent.Task
import scalaz.stream._

/** Turns instances of `S` into CSV sources.
  *
  * Any type `S` that has a implicit instance of `CsvSource` in scope will be enriched by the `asCsvSource` and
  * `asUnsafeCsvSource` methods (which map to [[source]] and [[unsafeSource]] respectively).
  *
  * Additionally, any type that has an instance of `CsvInput` in scope automatically gets an instance of `CsvSource`.
  */
@typeclass trait CsvSource[S] {
  @noop def toSource(s: S): Source

  @op("asCsvSource") def source[A: RowDecoder](s: S, sep: Char, header: Boolean): Process[Task, DecodeResult[A]] = {
    io.resource(Task.delay(toSource(s)))(src => Task.delay(src.close())) { src =>
      lazy val lines = CsvInput[Source].rows(src, sep, header)
      Task.delay { if(lines.hasNext) lines.next() else throw Cause.Terminated(Cause.End) }
    }
  }

  @op("asUnsafeCsvSource") def unsafeSource[A: RowDecoder](s: S, sep: Char, header: Boolean): Process[Task, A] =
    source(s, sep, header).map(_.get)
}

object CsvSource {
  implicit def fromInput[S: CsvInput]: CsvSource[S] = new CsvSource[S] {
    override def toSource(s: S): Source = CsvInput[S].toSource(s)
  }
}
