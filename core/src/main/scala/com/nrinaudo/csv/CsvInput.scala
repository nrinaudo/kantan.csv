package com.nrinaudo.csv

import java.io.{InputStream, File}

import simulacrum.{op, noop, typeclass}

import scala.io.{Codec, Source}

@typeclass trait CsvInput[S] {
  @noop def toSource(a: S): Source

  @op("asCsvRows") def rows[A: RowReader](s: S, separator: Char, header: Boolean): Iterator[Option[A]] = {
    val data = new CsvIterator(toSource(s), separator)
    if(header) data.drop(1).map(RowReader[A].read)
    else       data.map(RowReader[A].read)
  }

  @op("asUnsafeCsvRows") def unsafeRows[A: RowReader](s: S, separator: Char, header: Boolean): Iterator[A] =
    rows[A](s, separator, header).map(_.get)
}

object CsvInput {
  def apply[S](f: S => Source): CsvInput[S] = new CsvInput[S] {
    override def toSource(a: S): Source = f(a)
  }

  implicit def file(implicit codec: Codec): CsvInput[File] = CsvInput(f => Source.fromFile(f))
  implicit def inputStream(implicit codec: Codec): CsvInput[InputStream] = CsvInput(f => Source.fromInputStream(f))
  implicit val string: CsvInput[String] = CsvInput(s => Source.fromString(s))
  implicit val source: CsvInput[Source] = CsvInput(a => a)
}