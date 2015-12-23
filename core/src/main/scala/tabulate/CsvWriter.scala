package tabulate

import java.io.{Closeable, Writer}

import scala.annotation.tailrec

class CsvWriter[A] private[tabulate] (private val out: Writer, val sep: Char, private val format: A => Seq[String])
  extends Closeable {

  private def safeWrite(str: String): Unit = {
    @tailrec
    def escape(mark: Int, i: Int): Unit =
      if(i >= str.length) {
        if(mark != i) out.write(str, mark, i - mark)
      }
      else if(str.charAt(i) == '"') {
        out.write(str, mark, i - mark + 1)
        out.write('"')
        escape(i + 1, i + 1)
      }
      else escape(mark, i + 1)

    @tailrec
    def escapeIndex(index: Int): Int =
      if(index >= str.length) -1
      else {
        val c = str.charAt(index)
        if(c == '"' || c == sep || c == '\n' || c == '\r') index
        else escapeIndex(index + 1)
      }

    val index = escapeIndex(0)

    if(index == -1) out.write(str)
    else {
      out.write('"')
      out.write(str, 0, index)
      escape(index, index)
      out.write('"')
    }
  }

  private def write(ss: Seq[String]): Unit = {
    ss.headOption.foreach { h =>
      safeWrite(h)
      ss.tail.foreach { a =>
        out.write(sep.toInt)
        safeWrite(a)
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