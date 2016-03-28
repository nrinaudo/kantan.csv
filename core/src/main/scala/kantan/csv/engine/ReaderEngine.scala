package kantan.csv.engine

import java.io.Reader

import kantan.csv.{CsvReader, ReadResult}

/** Provides kantan.csv with CSV parsing functionalities.
  *
  * All methods that will need to create a new instance of [[CsvReader]] expect and rely on an implicit [[ReaderEngine]]
  * parameter. This allows third party libraries to plug into kantan.csv and replace the default parser at the cost
  * of a simple import.
  */
trait ReaderEngine {
  /** Turns the specified `Reader` into a `CsvReader`. */
  def readerFor(reader: Reader, separator: Char): CsvReader[ReadResult[Seq[String]]]
}

/** Provides instance creation methods and default implementations. */
object ReaderEngine {
  /** Creates a new [[ReaderEngine]] instance.
    *
    * Note that the `f` parameter can be tricky to get right without leaking exceptions. The recommended way of creating
    * new [[CsvReader]] instances from anything that might throw (because it's IO-bound) is to use
    * [[CsvReader.fromUnsafe]].
    *
    * @param f how to create new instances of [[CsvReader]].
    */
  def apply(f: (Reader, Char) ⇒ CsvReader[ReadResult[Seq[String]]]): ReaderEngine = new ReaderEngine {
    override def readerFor(reader: Reader, separator: Char): CsvReader[ReadResult[Seq[String]]] = f(reader, separator)
  }

  /** Default reader engine, used whenever a custom one is not explicitly brought in scope. */
  implicit val internal: ReaderEngine = ReaderEngine((reader, sep) ⇒ new InternalReader(reader, sep))
}
