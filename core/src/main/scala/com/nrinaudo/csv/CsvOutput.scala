package com.nrinaudo.csv

import java.io._

import simulacrum.{op, noop, typeclass}

import scala.io.Codec

@typeclass trait CsvOutput[S] { self =>
  @noop def toPrintWriter(s: S): PrintWriter

  @op("asCsvWriter") def writer[A: RowEncoder](s: S, separator: Char, header: Seq[String] = Seq.empty): CsvWriter[A] = {
    if(header.isEmpty) new CsvWriter[A](toPrintWriter(s), separator, RowEncoder[A].encode)
    else {
      val w = new CsvWriter(toPrintWriter(s), separator, identity[Seq[String]])
      w.write(header)
      w.contramap(RowEncoder[A].encode)
    }
  }

  @noop def contramap[T](f: T => S): CsvOutput[T] = CsvOutput(t => self.toPrintWriter(f(t)))
}

object CsvOutput {
  def apply[S](f: S => PrintWriter): CsvOutput[S] = new CsvOutput[S] {
    override def toPrintWriter(s: S): PrintWriter = f(s)
  }

  implicit def file(implicit codec: Codec): CsvOutput[File] =
    CsvOutput[OutputStream].contramap(f => new FileOutputStream(f))

  implicit def outputStream[O <: OutputStream](implicit codec: Codec): CsvOutput[O] =
    CsvOutput[Writer].contramap(o => new OutputStreamWriter(o, codec.charSet))

  implicit def writer[W <: Writer]: CsvOutput[W] = CsvOutput(w => new PrintWriter(w))

  implicit val printWriter: CsvOutput[PrintWriter] = new CsvOutput[PrintWriter] {
    override def toPrintWriter(s: PrintWriter) = s
  }
}