package com.nrinaudo

import java.io.{File, InputStream}

import scala.collection.mutable.ArrayBuffer
import scala.io.{Codec, Source}

package object csv {
  // - Unsafe parsers --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def unsafeRows(file: String, sep: Char)(implicit c: Codec): Iterator[ArrayBuffer[String]] =
    unsafeRows(Source.fromFile(file), sep)

  def unsafeRows(file: File, sep: Char)(implicit c: Codec): Iterator[ArrayBuffer[String]] =
    unsafeRows(Source.fromFile(file), sep)

  def unsafeRows(in: InputStream, sep: Char)(implicit c: Codec): Iterator[ArrayBuffer[String]] =
    unsafeRows(Source.fromInputStream(in), sep)

  def unsafeRows(source: Source, sep: Char): Iterator[ArrayBuffer[String]] =
    new CsvIterator(source, sep)



  // - Safe parsers --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Strictly equivalent to calling `rows[Vector[String]](file, sep)`. */
  def safeRows(file: File, sep: Char)(implicit c: Codec): Iterator[Vector[String]] =
    rows[Vector[String]](file, sep)

  /** Strictly equivalent to calling `rows[Vector[String]](in, sep)`. */
  def safeRows[A: RowReader](in: InputStream, sep: Char)(implicit c: Codec): Iterator[Vector[String]] =
    rows[Vector[String]](in, sep)

  /** Strictly equivalent to calling `rows[Vector[String]](file, sep)`. */
  def safeRows[A: RowReader](file: String, sep: Char)(implicit c: Codec): Iterator[Vector[String]] =
    rows[Vector[String]](file, sep)

  /** Strictly equivalent to calling `rows[Vector[String]](source, sep)`. */
  def safeRows[A: RowReader](source: Source, sep: Char): Iterator[Vector[String]] =
    rows[Vector[String]](source, sep)



  // - Typeclass-based parsers -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def rows[A: RowReader](file: File, sep: Char)(implicit c: Codec): Iterator[A] =
    rows(Source.fromFile(file), sep)

  def rows[A: RowReader](in: InputStream, sep: Char)(implicit c: Codec): Iterator[A] =
    rows(Source.fromInputStream(in), sep)

  def rows[A: RowReader](file: String, sep: Char)(implicit c: Codec): Iterator[A] =
    rows(Source.fromFile(file), sep)

  def rows[A: RowReader](source: Source, sep: Char): Iterator[A] =
    unsafeRows(source, sep).map(RowReader[A].read)
}
