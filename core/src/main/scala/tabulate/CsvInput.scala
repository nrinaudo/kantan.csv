package tabulate

import java.io._
import java.net.{URI, URL}

import simulacrum.{noop, op, typeclass}

import scala.io.{Codec, Source}

/** Turns instances of `S` into valid sources of CSV data.
  *
  * Any type `S` that has a implicit instance of `CsvInput` in scope will be enriched by the `asCsvRows` and
  * `asUnsafeCsvRows` methods (which map to [[rows]] and [[unsafeRows]] respectively).
  *
  * See the [[CsvInput$ companion object]] for default implementations and construction methods.
  */
@typeclass trait CsvInput[S] { self =>
  /** Turns the specified `S` into a `Reader`.
    *
    * Other methods in this trait all rely on this to open and parse CSV data.
    */
  @noop def open(s: S): Reader

  /** Turns the specified `S` into an iterator on `DecodeResult[A]`.
    *
    * This method is "safe", in that it does not throw exceptions when errors are encountered. This comes with the small
    * cost of having each row wrapped in a `DecodeResult` that then need to be unpacked. See [[unsafeRows]] for an
    * alternative.
    *
    * This method is also mapped to the `asCsvRows` one that enrich all types that have a valid `CsvInput` instance
    * in scope. For example:
    * {{{
    *   implicit val strInput: CsvInput[String] = ???
    *   "a,b,c".asCsvRows[List[Char]](',', false)
    * }}}
    *
    * @tparam A type to parse each row as.
    */
  @op("asCsvRows") def rows[A: RowDecoder](s: S, separator: Char, header: Boolean): CsvRows[DecodeResult[A]] =
    CsvRows(open(s), separator, header)

  /** Turns the specified `S` into an iterator on `A`.
    *
    * This is the "unsafe" version of [[rows]]: it will throw as soon as an error is encountered.
    *
    * @tparam A type to parse each row as.
    */
  @op("asUnsafeCsvRows") def unsafeRows[A: RowDecoder](s: S, separator: Char, header: Boolean): CsvRows[A] =
    rows[A](s, separator, header).map(_.getOrElse(throw new IOException("Illegal CSV data found")))

  /** Turns an instance of `CsvInput[S]` into one of `CsvInput[T]`.
    *
    * This allows developers to adapt existing instances of `CsvInput` rather than write one from scratch.
    * One could, for example, write `CsvInput[URL]` by basing it on `CsvInput[InputStream]`:
    * {{{
    *   val urlInput: CsvInput[URL] = CsvInput[InputStream].contramap((url: URL) => url.openStream())
    * }}}
    */
  @noop def contramap[T](f: T => S): CsvInput[T] = CsvInput.fromReader(t => self.open(f(t)))
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
  def fromReader[S](f: S => Reader): CsvInput[S] = new CsvInput[S] {
    override def open(a: S) = f(a)
  }

  def fromStream[S](f: S => InputStream)(implicit codec: Codec): CsvInput[S] = new CsvInput[S] {
    override def open(s: S) = new InputStreamReader(f(s), codec.charSet)
  }

  /** Turns any `java.io.File` into a source of CSV data. */
  implicit def file(implicit codec: Codec): CsvInput[File] = fromStream(f => new FileInputStream(f))
  /** Turns any array of bytes into a source of CSV data. */
  implicit def bytes(implicit codec: Codec): CsvInput[Array[Byte]] = fromStream(b => new ByteArrayInputStream(b))
  /** Turns any `java.net.URL` into a source of CSV data. */
  implicit def url(implicit codec: Codec): CsvInput[URL] = fromStream(u => u.openStream())
  /** Turns any `java.net.URI` into a source of CSV data. */
  implicit def uri(implicit codec: Codec): CsvInput[URI] = file.contramap(u => new File(u))
  /** Turns any array of chars into a source of CSV data. */
  implicit val chars: CsvInput[Array[Char]] = fromReader(c => new CharArrayReader(c))
  /** Turns any string into a source of CSV data. */
  implicit val string: CsvInput[String] = fromReader(s => new StringReader(s))
}