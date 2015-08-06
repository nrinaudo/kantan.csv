package com.nrinaudo.csv

import java.io.{Closeable, PrintWriter}

class CsvWriter[A] private[csv] (private val out: PrintWriter, val sep: Char, private val format: A => List[String])
  extends Closeable {

  private def escape(str: String): String =
    if(str.contains("\""))                                                  "\"" + str.replaceAll("\"", "\"\"") + "\""
    else if (str.contains(sep) || str.contains("\n") || str.contains("\r")) "\"" + str + "\""
    else                                                                    str

  private def write(ss: List[String]): Unit = ss.map(escape) match {
    case h :: t =>
      out.print(h)
      t.foreach { a =>
        out.print(sep)
        out.print(a)
      }
      out.print("\r\n") // According to the RFC, \n alone is not valid.
    case _ => // Empty rows are not printed.
  }

  def write(a: A): Unit = write(format(a))

  override def close(): Unit = out.close()

  def contramap[B](f: B => A): CsvWriter[B] = new CsvWriter[B](out, sep, f andThen format)
}
