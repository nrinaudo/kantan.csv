package com.nrinaudo.csv

import java.io._

import simulacrum.typeclass

import scala.io.Codec

@typeclass trait CsvOutput[S] {
  def printWriter(s: S): PrintWriter
  def sink[A: RowWriter](s: S, separator: Char, header: Seq[String] = Seq.empty): CsvWriter[A] = {
    if(header.isEmpty) new CsvWriter[A](printWriter(s), separator, RowWriter[A].write)
    else {
      val w = new CsvWriter(printWriter(s), separator, identity[Seq[String]])
      w.write(header)
      w.contramap(RowWriter[A].write)
    }
  }
}

object CsvOutput {
  def apply[S](f: S => PrintWriter): CsvOutput[S] = new CsvOutput[S] {
    override def printWriter(s: S): PrintWriter = f(s)
  }

  implicit def file(implicit codec: Codec): CsvOutput[File] =
    CsvOutput(f => new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), codec.charSet)))

  implicit def outputStream(implicit codec: Codec): CsvOutput[OutputStream] =
      CsvOutput(o => new PrintWriter(new OutputStreamWriter(o, codec.charSet)))

  implicit def writer[A <: Writer]: CsvOutput[A] = CsvOutput(w => new PrintWriter(w))

  implicit val printWriter: CsvOutput[PrintWriter] = new CsvOutput[PrintWriter] {
    override def printWriter(s: PrintWriter): PrintWriter = s
  }
}