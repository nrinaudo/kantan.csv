package com.nrinaudo

import java.io.{File, InputStream}

import scala.collection.mutable.ArrayBuffer
import scala.io.{Codec, Source}

package object csv {
  // - Unsafe parsers --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def unsafe(file: String, sep: Char)(implicit c: Codec): Iterator[ArrayBuffer[String]] =
    unsafe(Source.fromFile(file), sep)

  def unsafe(file: File, sep: Char)(implicit c: Codec): Iterator[ArrayBuffer[String]] =
    unsafe(Source.fromFile(file), sep)

  def unsafe(in: InputStream, sep: Char)(implicit c: Codec): Iterator[ArrayBuffer[String]] =
    unsafe(Source.fromInputStream(in), sep)

  def unsafe(source: Source, sep: Char): Iterator[ArrayBuffer[String]] =
    new CsvIterator(source, sep)



  // - Safe parsers --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def safe(file: File, sep: Char)(implicit c: Codec): Iterator[Vector[String]] =
    safe(Source.fromFile(file), sep)

  def safe(in: InputStream, sep: Char)(implicit c: Codec): Iterator[Vector[String]] =
    safe(Source.fromInputStream(in), sep)

  def safe(file: String, sep: Char)(implicit c: Codec): Iterator[Vector[String]] =
    safe(Source.fromFile(file), sep)

  def safe(source: Source, sep: Char): Iterator[Vector[String]] =
    unsafe(source, sep).map(_.toVector)
}
