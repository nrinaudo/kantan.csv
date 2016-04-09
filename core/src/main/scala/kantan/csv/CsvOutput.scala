package kantan.csv

import java.io._
import java.nio.file.{Files, Path}
import kantan.csv.engine.WriterEngine
import scala.io.Codec

/** Type class for all types that can be turned into [[CsvWriter]] instances.
  *
  * Instances of [[CsvOutput]] are rarely used directly. The preferred, idiomatic way is to use the implicit syntax
  * provided by [[ops.CsvOutputOps CsvOutputOps]], brought in scope by importing `kantan.csv.ops._`.
  *
  * See the [[CsvOutput companion object]] for default implementations and construction methods.
  */
trait CsvOutput[-S] extends Serializable { self ⇒
  /** Opens a `Writer` on the specified `S`. */
  def open(s: S): Writer

  /** Opens a [[CsvWriter]] on the specified `S`.
    *
    * @param s what to open a [[CsvWriter]] on.
    * @param sep column separator.
    * @param header optional header row, defaults to none.
    */
  def writer[A: RowEncoder](s: S, sep: Char, header: Seq[String] = Seq.empty)(implicit e: WriterEngine): CsvWriter[A] =
    CsvWriter(open(s), sep, header)

  /** Writes the specified collections directly in the specifie `S`.
    *
    * @param s where to write the CSV data.
    * @param rows CSV data to encode and serialise.
    * @param sep column separator.
    * @param header optional header row, defaults to none.
    */
  def write[A: RowEncoder](s: S, rows: TraversableOnce[A], sep: Char, header: Seq[String] = Seq.empty)
                          (implicit e: WriterEngine): Unit =
    writer(s, sep, header).write(rows).close()

  /** Turns a `CsvOutput[S]` into a `CsvOutput[T]`.
    *
    * This allows developers to adapt existing instances of [[CsvOutput]] rather than write one from scratch.
    * One could, for example, write `CsvInput[File]` by basing it on `CsvInput[OutputStream]`:
    * {{{
    *   def fileOutput(implicit c: scala.io.Codec): CsvOutput[File] =
    *     CsvOutput[OutputStream].contramap(f ⇒ new FileOutputStream(f, c.charSet))
    * }}}
    */
  def contramap[T](f: T ⇒ S): CsvOutput[T] = CsvOutput(t ⇒ self.open(f(t)))
}

/** Provides default instances as well as instance summoning and creation methods. */
object CsvOutput {
  /** Summons an implicit instance of `CsvOutput[A]` if one can be found.
    *
    * This is simply a convenience method. The two following calls are equivalent:
    * {{{
    *   val file: CsvOutput[File] = CsvOutput[File]
    *   val file2: CsvOutput[File] = implicitly[CsvOutput[File]]
    * }}}
    */
  def apply[A](implicit oa: CsvOutput[A]): CsvOutput[A] = oa

  /** Turns the specified function into a [[CsvOutput]].
    *
    * Note that it's usually better to compose an existing instance through [[CsvOutput.contramap]] rather than create
    * one from scratch.
    */
  def apply[A](f: A ⇒ Writer): CsvOutput[A] = new CsvOutput[A] {
    override def open(s: A): Writer = f(s)
  }

  /** Default implementation for `Writer`. */
  implicit def writer: CsvOutput[Writer] = CsvOutput(w ⇒ w)

  /** Default implementation for `OutputStream`. */
  implicit def outputStream(implicit codec: Codec): CsvOutput[OutputStream] =
    writer.contramap(o ⇒ new BufferedWriter(new OutputStreamWriter(o, codec.charSet)))

  /** Default implementation for `File`. */
  implicit def file(implicit codec: Codec): CsvOutput[File] = outputStream.contramap(f ⇒ new FileOutputStream(f))

  implicit def path(implicit codec: Codec): CsvOutput[Path] =
    writer.contramap(p ⇒ Files.newBufferedWriter(p, codec.charSet))
}
