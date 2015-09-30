package com.nrinaudo.csv

import java.io.{InputStream, File}

import simulacrum.typeclass

import scala.io.{Codec, Source}

@typeclass trait CsvInput[S] {
  def source(a: S): Source
  def rows[A: RowReader](s: S, separator: Char, header: Boolean): Iterator[A] = {
    val data = new CsvIterator(source(s), separator)
    if(header) data.drop(1).map(RowReader[A].read)
    else       data.map(RowReader[A].read)
  }
}

object CsvInput {
  def apply[S](f: S => Source): CsvInput[S] = new CsvInput[S] {
    override def source(a: S): Source = f(a)
  }

  implicit def file(implicit codec: Codec): CsvInput[File] = CsvInput(f => Source.fromFile(f))
  implicit def inputStream(implicit codec: Codec): CsvInput[InputStream] = CsvInput(f => Source.fromInputStream(f))
  implicit val string: CsvInput[String] = CsvInput(s => Source.fromString(s))
  implicit val source: CsvInput[Source] = CsvInput(a => a)
}