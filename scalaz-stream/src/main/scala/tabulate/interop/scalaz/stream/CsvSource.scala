package tabulate.interop.scalaz.stream

import java.io.Reader

import simulacrum.{noop, op, typeclass}
import tabulate._
import tabulate.engine.ReaderEngine

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
  @noop def reader(s: S): Reader

  @op("asCsvSource") def source[A: RowDecoder](s: S, sep: Char, header: Boolean)(implicit engine: ReaderEngine): Process[Task, DecodeResult[A]] =
    CsvSource[A](reader(s), sep, header)

  @op("asUnsafeCsvSource") def unsafeSource[A: RowDecoder](s: S, sep: Char, header: Boolean): Process[Task, A] =
    source(s, sep, header).map(_.get)
}

object CsvSource {
  def apply[A](reader: => CsvReader[A]): Process[Task, A] =
    io.iteratorR(Task.delay(reader))(csv => Task.delay(csv.close()))(csv => Task.delay(csv.toIterator))

  def apply[A: RowDecoder](reader: => Reader, sep: Char, header: Boolean)(implicit engine: ReaderEngine): Process[Task, DecodeResult[A]] =
    CsvSource(CsvReader[A](reader, sep, header))

  implicit def fromInput[S](implicit is: CsvInput[S]): CsvSource[S] = new CsvSource[S] {
    override def reader(s: S) = is.reader(s)
  }
}
