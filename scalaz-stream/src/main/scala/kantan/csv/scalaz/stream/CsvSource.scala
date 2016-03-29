package kantan.csv.scalaz.stream

import _root_.scalaz.concurrent.Task
import _root_.scalaz.stream._
import java.io.Reader
import kantan.csv._
import kantan.csv.engine.ReaderEngine

/** Turns instances of `S` into CSV sources.
  *
  * Any type `S` that has a implicit instance of [[CsvSource]] in scope will be enriched by the `asCsvSource` and
  * `asUnsafeCsvSource` methods (which map to [[source]] and [[unsafeSource]] respectively).
  *
  * Additionally, any type that has an instance of `CsvInput` in scope automatically gets an instance of [[CsvSource]].
  */
trait CsvSource[S] extends Serializable {
  def reader(s: S): ParseResult[Reader]

  def source[A: RowDecoder](s: S, sep: Char, header: Boolean)
                           (implicit engine: ReaderEngine): Process[Task, ReadResult[A]] =
    CsvSource[A](reader(s).get, sep, header)

  def unsafeSource[A: RowDecoder](s: S, sep: Char, header: Boolean)(implicit engine: ReaderEngine): Process[Task, A] =
    source(s, sep, header).map(_.get)
}

object CsvSource {
  def apply[A](implicit sa: CsvSource[A]): CsvSource[A] = sa

  def apply[A](reader: ⇒ CsvReader[A]): Process[Task, A] =
    io.iteratorR(Task.delay(reader))(csv ⇒ Task.delay(csv.close()))(csv ⇒ Task.delay(csv.toIterator))

  def apply[A: RowDecoder](reader: ⇒ Reader, sep: Char, header: Boolean)
                          (implicit engine: ReaderEngine): Process[Task, ReadResult[A]] =
    CsvSource(CsvReader[A](reader, sep, header))

  implicit def fromInput[S](implicit is: CsvInput[S]): CsvSource[S] = new CsvSource[S] {
    override def reader(s: S) = is.open(s)
  }
}
