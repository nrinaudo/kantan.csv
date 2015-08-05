package com.nrinaudo

import java.io._

import scala.collection.mutable.ArrayBuffer
import scala.io.{Codec, Source}

package object csv {
  // - Unsafe parsers --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def unsafeRowsR(file: String, sep: Char)(implicit c: Codec): Iterator[ArrayBuffer[String]] =
    unsafeRowsR(Source.fromFile(file), sep)

  def unsafeRowsR(file: File, sep: Char)(implicit c: Codec): Iterator[ArrayBuffer[String]] =
    unsafeRowsR(Source.fromFile(file), sep)

  def unsafeRowsR(in: InputStream, sep: Char)(implicit c: Codec): Iterator[ArrayBuffer[String]] =
    unsafeRowsR(Source.fromInputStream(in), sep)

  def unsafeRowsR(source: Source, sep: Char): Iterator[ArrayBuffer[String]] =
    new CsvIterator(source, sep)



  // - Safe parsers --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Strictly equivalent to calling `rows[Vector[String]](file, sep)`. */
  def safeRowsR(file: File, sep: Char)(implicit c: Codec): Iterator[Vector[String]] =
    rowsR[Vector[String]](file, sep)

  /** Strictly equivalent to calling `rows[Vector[String]](in, sep)`. */
  def safeRowsR[A: RowReader](in: InputStream, sep: Char)(implicit c: Codec): Iterator[Vector[String]] =
    rowsR[Vector[String]](in, sep)

  /** Strictly equivalent to calling `rows[Vector[String]](file, sep)`. */
  def safeRowsR[A: RowReader](file: String, sep: Char)(implicit c: Codec): Iterator[Vector[String]] =
    rowsR[Vector[String]](file, sep)

  /** Strictly equivalent to calling `rows[Vector[String]](source, sep)`. */
  def safeRowsR[A: RowReader](source: Source, sep: Char): Iterator[Vector[String]] =
    rowsR[Vector[String]](source, sep)



  // - Typeclass-based parsers -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def rowsR[A: RowReader](file: File, sep: Char)(implicit c: Codec): Iterator[A] =
    rowsR(Source.fromFile(file), sep)

  def rowsR[A: RowReader](in: InputStream, sep: Char)(implicit c: Codec): Iterator[A] =
    rowsR(Source.fromInputStream(in), sep)

  def rowsR[A: RowReader](file: String, sep: Char)(implicit c: Codec): Iterator[A] =
    rowsR(Source.fromFile(file), sep)

  def rowsR[A: RowReader](source: Source, sep: Char): Iterator[A] =
    unsafeRowsR(source, sep).map(RowReader[A].read)



  // - Typeclass-based writers -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def rowsW[A](out: PrintWriter, sep: Char)(implicit rw: RowWriter[A]): CsvWriter[A] = {
    rw.header.fold(new CsvWriter[A](out, sep, rw.write)) { h =>
      val w = new CsvWriter[List[String]](out, sep, identity)
      w.write(h)
      w.contramap(rw.write)
    }
  }

  def rowsW[A: RowWriter](file: File, sep: Char)(implicit c: Codec): CsvWriter[A] =
    rowsW(new FileOutputStream(file), sep)

  def rowsW[A: RowWriter](file: String, sep: Char)(implicit c: Codec): CsvWriter[A] =
    rowsW(new FileOutputStream(file), sep)

  def rowsW[A: RowWriter](out: OutputStream, sep: Char)(implicit c: Codec): CsvWriter[A] =
    rowsW(new PrintStream(out, true, c.charSet.name()), sep)

  def rowsW[A: RowWriter](out: PrintStream, sep: Char): CsvWriter[A] =
    rowsW(new PrintWriter(out), sep)
}
