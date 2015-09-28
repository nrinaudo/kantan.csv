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



  // - Typeclass-based parsers -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Opens the specified file as CSV data.
    *
    * @param file   file to open.
    * @param sep    CSV separator. Commas are usually a good default value, but Microsoft Excel defaults to environment
    *               dependant values.
    * @param header whether or not the specified file contains a header row.
    */
  def rowsR[A: RowReader](file: File, sep: Char, header: Boolean)(implicit c: Codec): Iterator[A] =
    rowsR(Source.fromFile(file), sep, header)

  /** Opens the specified stream as CSV data.
    *
    * @param in     stream to open.
    * @param sep    CSV separator. Commas are usually a good default value, but Microsoft Excel defaults to environment
    *               dependant values.
    * @param header whether or not the specified file contains a header row.
    */
  def rowsR[A: RowReader](in: InputStream, sep: Char, header: Boolean)(implicit c: Codec): Iterator[A] =
    rowsR(Source.fromInputStream(in), sep, header)

  /** Opens the file denoted by the specified path as CSV data.
    *
    * @param file   name of the file to open.
    * @param sep    CSV separator. Commas are usually a good default value, but Microsoft Excel defaults to environment
    *               dependant values.
    * @param header whether or not the specified file contains a header row.
    */
  def rowsR[A: RowReader](file: String, sep: Char, header: Boolean)(implicit c: Codec): Iterator[A] =
    rowsR(Source.fromFile(file), sep, header)

  /** Opens the specified source as CSV data.
    *
    * @param source source to open.
    * @param sep    CSV separator. Commas are usually a good default value, but Microsoft Excel defaults to environment
    *               dependant values.
    * @param header whether or not the specified file contains a header row.
    */
  def rowsR[A](source: Source, sep: Char, header: Boolean)(implicit r: RowReader[A]): Iterator[A] = {
    val data = unsafeRowsR(source, sep)
    if(header) data.drop(1).map(r.read)
    else       data.map(r.read)
  }



  // - Typeclass-based writers -----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def rowsW[A](out: PrintWriter, sep: Char, header: String*)(implicit rw: RowWriter[A]): CsvWriter[A] = {
    if(header.isEmpty) new CsvWriter[A](out, sep, rw.write)
    else {
      val w = new CsvWriter(out, sep, identity[Seq[String]])
      w.write(header)
      w.contramap(rw.write)
    }
  }

  def rowsW[A: RowWriter](file: File, sep: Char, header: String*)(implicit c: Codec): CsvWriter[A] =
    rowsW(new FileOutputStream(file), sep, header:_*)

  def rowsW[A: RowWriter](file: String, sep: Char, header: String*)(implicit c: Codec): CsvWriter[A] =
    rowsW(new FileOutputStream(file), sep, header:_*)

  def rowsW[A: RowWriter](out: OutputStream, sep: Char, header: String*)(implicit c: Codec): CsvWriter[A] =
    rowsW(new PrintWriter(new PrintStream(out, true, c.charSet.name())), sep, header:_*)

  def rowsW[A: RowWriter](out: PrintStream, sep: Char, header: String*): CsvWriter[A] =
    rowsW(new PrintWriter(out), sep, header:_*)
}
