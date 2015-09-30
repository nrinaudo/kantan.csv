package com.nrinaudo.csv

import java.io._

import simulacrum.{op, noop, typeclass}

import scala.io.Codec

@typeclass trait CsvOutput[S] {
  @noop def toPrintWriter(s: S): PrintWriter

  @op("asCsvWriter") def writer[A: RowWriter](s: S, separator: Char, header: Seq[String] = Seq.empty): CsvWriter[A] = {
    if(header.isEmpty) new CsvWriter[A](toPrintWriter(s), separator, RowWriter[A].write)
    else {
      val w = new CsvWriter(toPrintWriter(s), separator, identity[Seq[String]])
      w.write(header)
      w.contramap(RowWriter[A].write)
    }
  }
}

object CsvOutput {
  def apply[S](f: S => PrintWriter): CsvOutput[S] = new CsvOutput[S] {
    override def toPrintWriter(s: S): PrintWriter = f(s)
  }

  implicit def file(implicit codec: Codec): CsvOutput[File] =
    CsvOutput(f => new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), codec.charSet)))

  implicit def outputStream(implicit codec: Codec): CsvOutput[OutputStream] =
      CsvOutput(o => new PrintWriter(new OutputStreamWriter(o, codec.charSet)))

  implicit def writer[A <: Writer]: CsvOutput[A] = CsvOutput(w => new PrintWriter(w))

  implicit val printWriter: CsvOutput[PrintWriter] = new CsvOutput[PrintWriter] {
    override def toPrintWriter(s: PrintWriter): PrintWriter = s
  }
}