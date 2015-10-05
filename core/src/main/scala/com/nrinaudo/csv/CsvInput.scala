package com.nrinaudo.csv

import java.io.{IOException, File, InputStream}
import java.net.{URI, URL}

import simulacrum.{noop, op, typeclass}

import scala.io.{Codec, Source}

@typeclass trait CsvInput[S] { self =>
  @noop def toSource(a: S): Source

  @op("asCsvRows") def rows[A: RowDecoder](s: S, separator: Char, header: Boolean): Iterator[DecodeResult[A]] = {
    val data = new CsvIterator(toSource(s), separator)
    if(header) data.drop(1).map(r => r.flatMap(RowDecoder[A].decode))
    else       data.map(r => r.flatMap(RowDecoder[A].decode))
  }

  @op("asUnsafeCsvRows") def unsafeRows[A: RowDecoder](s: S, separator: Char, header: Boolean): Iterator[A] =
    rows[A](s, separator, header).map(_.get)

  @noop def contramap[T](f: T => S): CsvInput[T] = CsvInput(t => self.toSource(f(t)))
}

object CsvInput {
  def apply[S](f: S => Source): CsvInput[S] = new CsvInput[S] {
    override def toSource(a: S): Source = f(a)
  }

  implicit def file(implicit codec: Codec): CsvInput[File] = CsvInput(f => Source.fromFile(f))
  implicit def inputStream[I <: InputStream](implicit codec: Codec): CsvInput[I] = CsvInput(i => Source.fromInputStream(i))
  implicit def bytes(implicit codec: Codec): CsvInput[Array[Byte]] = CsvInput(bs => Source.fromBytes(bs))
  implicit def url(implicit codec: Codec): CsvInput[URL] = CsvInput(u => Source.fromURL(u))
  implicit def uri(implicit codec: Codec): CsvInput[URI] = CsvInput(u => Source.fromURI(u))
  implicit val chars: CsvInput[Array[Char]] = CsvInput(cs => Source.fromChars(cs))
  implicit val string: CsvInput[String] = CsvInput(s => Source.fromString(s))
  implicit val source: CsvInput[Source] = CsvInput(s => s)
}