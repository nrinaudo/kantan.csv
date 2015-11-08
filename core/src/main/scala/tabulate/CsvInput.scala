package tabulate

import java.io.{InputStream, File}
import java.net.{URI, URL}

import simulacrum.{op, noop, typeclass}

import scala.io.{Codec, Source}

/** Turns instances of `S` into valid sources of CSV data.
  *
  * Any type `S` that has a implicit instance of `CsvInput` in scope will be enriched by the `asCsvRows` and
  * `asUnsafeCsvRows` methods (which map to [[rows]] and [[unsafeRows]] respectively).
  *
  * See the [[CsvInput$ companion object]] for default implementations and construction methods.
  */
@typeclass trait CsvInput[S] { self =>
  /** Turns the specified `S` into a source.
    *
    * Other methods in this trait all rely on this to open and parse CSV data.
    */
  @noop def toSource(s: S): Source

  /** Turns the specified `S` into an iterator on `DecodeResult[A]`.
    *
    * This method is "safe", in that it does not throw exceptions when errors are encountered. This comes with the small
    * cost of having each row wrapped in a `DecodeResult` that then need to be unpacked. See [[unsafeRows]] for an
    * alternative.
    *
    * This method is also mapped to the `asCsvRows` one that enrich all types that have a valid `CsvInput` instance
    * in scope. For example:
    * {{{
    *   implicit val strSource: CsvSource[String] = ???
    *   "a,b,c".asCsvRows[List[Char]](',', false)
    * }}}
    *
    * @tparam A type to parse each row as.
    */
  @op("asCsvRows") def rows[A: RowDecoder](s: S, separator: Char, header: Boolean): Iterator[DecodeResult[A]] = {
    val data = new CsvIterator(toSource(s), separator)
    if(header) data.drop(1).map(r => r.flatMap(RowDecoder[A].decode))
    else       data.map(r => r.flatMap(RowDecoder[A].decode))
  }

  /** Turns the specified `S` into an iterator on `A`.
    *
    * This is the "unsafe" version of [[rows]]: it will throw as soon as an error is encountered.
    *
    * @tparam A type to parse each row as.
    */
  @op("asUnsafeCsvRows") def unsafeRows[A: RowDecoder](s: S, separator: Char, header: Boolean): Iterator[A] =
    rows[A](s, separator, header).map(_.get)

  /** Turns an instance of `CsvInput[S]` into one of `CsvInput[T]`.
    *
    * This allows developers to adapt existing instances of `CsvInput` rather than write one from scratch.
    * One could, for example, write `CsvInput[URL]` by basing it on `CsvInput[InputStream]`:
    * {{{
    *   val urlInput: CsvInput[URL] = CsvInput[InputStream].contramap((url: URL) => url.openStream())
    * }}}
    */
  @noop def contramap[T](f: T => S): CsvInput[T] = CsvInput(t => self.toSource(f(t)))
}

@export.imports[CsvInput]
trait LowPriorityCsvInputs

/** Defines convenience methods for creating and retrieving instances of `CsvInput`.
  *
  * Implicit default implementations of standard types are also declared here, always bringing them in scope with a low
  * priority.
  *
  * These default implementations can also be useful when writing more complex instances: if you need to write a
  * `CsvInput[T]` and have both a `CsvInput[S]` and a `T => S`, you need just use [[CsvInput.contramap]] to create
  * your implementation.
  */
object CsvInput extends LowPriorityCsvInputs {
  /** Creates an instance of `CsvInput[S]` from the specified function. */
  def apply[S](f: S => Source): CsvInput[S] = new CsvInput[S] {
    override def toSource(a: S): Source = f(a)
  }

  /** Turns any `java.io.File` into a source of CSV data. */
  implicit def file(implicit codec: Codec): CsvInput[File] = CsvInput(f => Source.fromFile(f))
  /** Turns any `java.io.InputStream` into a source of CSV data. */
  implicit def inputStream[I <: InputStream](implicit codec: Codec): CsvInput[I] = CsvInput(i => Source.fromInputStream(i))
  /** Turns any array of bytes into a source of CSV data. */
  implicit def bytes(implicit codec: Codec): CsvInput[Array[Byte]] = CsvInput(bs => Source.fromBytes(bs))
  /** Turns any `java.net.URL` into a source of CSV data. */
  implicit def url(implicit codec: Codec): CsvInput[URL] = CsvInput(u => Source.fromURL(u))
  /** Turns any `java.net.URI` into a source of CSV data. */
  implicit def uri(implicit codec: Codec): CsvInput[URI] = CsvInput(u => Source.fromURI(u))
  /** Turns any array of chars into a source of CSV data. */
  implicit val chars: CsvInput[Array[Char]] = CsvInput(cs => Source.fromChars(cs))
  /** Turns any string into a source of CSV data. */
  implicit val string: CsvInput[String] = CsvInput(s => Source.fromString(s))
  /** Turns any `scala.io.Source` into a source of CSV data. */
  implicit val source: CsvInput[Source] = CsvInput(s => s)
}