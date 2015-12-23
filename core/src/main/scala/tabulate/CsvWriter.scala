package tabulate

import java.io.{Writer, Closeable, PrintWriter}

class CsvWriter[A] private[tabulate] (private val out: Writer, val sep: Char, private val format: A => Seq[String])
  extends Closeable {

  private def escape(str: String): String =
    if(str.contains("\""))                                                 s""""${str.replaceAll("\"", "\"\"")}""""
    else if(str.contains(sep) || str.contains("\n") || str.contains("\r")) s""""$str""""
    else                                                                   str

  private def write(ss: Seq[String]): Unit = {
    val fs = ss.map(escape)
    fs.headOption.foreach { h =>
      out.write(h)
      fs.tail.foreach { a =>
        out.write(sep.toInt)
        out.write(a)
      }
      out.write("\r\n") // According to the RFC, \n alone is not valid.
    }
  }

  def write(a: A): CsvWriter[A] = {
    write(format(a))
    out.flush()
    this
  }

  override def close(): Unit = out.close()

  def contramap[B](f: B => A): CsvWriter[B] = new CsvWriter[B](out, sep, f andThen format)
}