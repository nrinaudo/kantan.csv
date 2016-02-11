package tabulate

import java.io._
import java.net.{URI, URL}

import simulacrum.{op, noop, typeclass}
import tabulate.engine.ReaderEngine

import scala.collection.generic.CanBuildFrom
import scala.io.Codec

/** Turns instances of `S` into valid sources of CSV data.
  *
  * Any type `S` that has a implicit instance of [[CsvInput]] in scope will be enriched by the `asCsvReader` and
  * `asUnsafeCsvReader` methods (which map to [[reader]] and [[unsafeReader]] respectively).
  *
  * See the [[CsvInput$ companion object]] for default implementations and construction methods.
  */
@typeclass trait CsvInput[-S] { self ⇒
  /** Turns the specified `S` into a `Reader`.
    *
    * Other methods in this trait all rely on this to open and parse CSV data.
    */
  @noop
  def open(s: S): Reader

  /** Turns the specified `S` into an iterator on `DecodeResult[A]`.
    *
    * This method is "safe", in that it does not throw exceptions when errors are encountered. This comes with the small
    * cost of having each row wrapped in a [[DecodeResult]] that then need to be unpacked. See [[unsafeReader]] for an
    * alternative.
    *
    * This method is also mapped to the `asCsvRows` one that enrich all types that have a valid [[CsvInput]] instance
    * in scope. For example:
    * {{{
    *   implicit val strInput: CsvInput[String] = ???
    *   "a,b,c".asCsvRows[List[Char]](',', false)
    * }}}
    *
    * @tparam A type to parse each row as.
    */
  @op("asCsvReader")
  def reader[A: RowDecoder](s: S, separator: Char, header: Boolean)(implicit engine: ReaderEngine): CsvReader[DecodeResult[A]] =
    CsvReader(open(s), separator, header)

  /** Turns the specified `S` into an iterator on `A`.
    *
    * This is the "unsafe" version of [[reader]]: it will throw as soon as an error is encountered.
    *
    * @tparam A type to parse each row as.
    */
  @op("asUnsafeCsvReader")
  def unsafeReader[A: RowDecoder](s: S, separator: Char, header: Boolean)(implicit engine: ReaderEngine): CsvReader[A] =
    reader[A](s, separator, header).map(_.getOrElse(throw new IOException("Illegal CSV data found")))

  @op("readCsv")
  def read[C[_], A: RowDecoder](s: S, sep: Char, header: Boolean)(implicit e: ReaderEngine, cbf: CanBuildFrom[Nothing, DecodeResult[A], C[DecodeResult[A]]]): C[DecodeResult[A]] =
    reader(s, sep, header).to[C]

  @op("unsafeReadCsv")
  def unsafeRead[C[_], A: RowDecoder](s: S, sep: Char, header: Boolean)(implicit e: ReaderEngine, cbf: CanBuildFrom[Nothing, A, C[A]]): C[A] =
    unsafeReader(s, sep, header).to[C]


  /** Turns an instance of `CsvInput[S]` into one of `CsvInput[T]`.
    *
    * This allows developers to adapt existing instances of [[CsvInput]] rather than write one from scratch.
    * One could, for example, write `CsvInput[URL]` by basing it on `CsvInput[InputStream]`:
    * {{{
    *   val urlInput: CsvInput[URL] = CsvInput[InputStream].contramap((url: URL) ⇒ url.openStream())
    * }}}
    */
  @noop def contramap[T](f: T ⇒ S): CsvInput[T] = CsvInput((t: T) ⇒ self.open(f(t)))
}

@export.imports[CsvInput]
trait LowPriorityCsvInputs

/** Defines convenience methods for creating and retrieving instances of [[CsvInput]].
  *
  * Implicit default implementations of standard types are also declared here, always bringing them in scope with a low
  * priority.
  *
  * These default implementations can also be useful when writing more complex instances: if you need to write a
  * `CsvInput[T]` and have both a `CsvInput[S]` and a `T ⇒ S`, you need just use [[CsvInput.contramap]] to create
  * your implementation.
  */
object CsvInput extends LowPriorityCsvInputs {
  def apply[A](f: A ⇒ Reader): CsvInput[A] = new CsvInput[A] {
      override def open(a: A): Reader = f(a)
    }

  /** Turns any `java.io.Reader` into a source of CSV data. */
  implicit def reader: CsvInput[Reader] = CsvInput(r ⇒ r)

  /** Turns any `java.io.InputStream` into a source of CSV data. */
  implicit def inputStream(implicit codec: Codec): CsvInput[InputStream] =
    reader.contramap(i ⇒ new InputStreamReader(i, codec.charSet))
  /** Turns any `java.io.File` into a source of CSV data. */
  implicit def file(implicit codec: Codec): CsvInput[File] = inputStream.contramap(f ⇒ new FileInputStream(f))
  /** Turns any array of bytes into a source of CSV data. */
  implicit def bytes(implicit codec: Codec): CsvInput[Array[Byte]] = inputStream.contramap(bs ⇒ new ByteArrayInputStream(bs))
  /** Turns any `java.net.URL` into a source of CSV data. */
  implicit def url(implicit codec: Codec): CsvInput[URL] = inputStream.contramap(_.openStream())
  /** Turns any `java.net.URI` into a source of CSV data. */
  implicit def uri(implicit codec: Codec): CsvInput[URI] = url.contramap(_.toURL)
  /** Turns any array of chars into a source of CSV data. */
  implicit val chars: CsvInput[Array[Char]] = reader.contramap(cs ⇒ new CharArrayReader(cs))
  /** Turns any string into a source of CSV data. */
  implicit val string: CsvInput[String] = reader.contramap(s ⇒ new StringReader(s))
}