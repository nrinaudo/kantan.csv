package com.nrinaudo

import java.io.IOException

import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scalaz.concurrent.Task
import scalaz.stream._

object csv {
  private class EntryIterator(data: Iterator[Char], separator: Char) extends Iterator[ArrayBuffer[String]] {
    private[this] val entry = new StringBuilder
    private[this] val row = ArrayBuffer[String]()
    private[this] var state = 0
    // 0: normal, 1: in escape, 2: leaving escape
    private[this] val input: BufferedIterator[Char] = data.buffered
    var wCount = 0 // Number of whitespace found at the end of and escaped cell.

    /** Appends the content of the specified cell to the specified row builder. */
    private def appendEntry(trim: Boolean) = {
      row += { if(trim) entry.result().trim else entry.result() }
      entry.clear()
      row
    }

    private def isLineBreak(c: Char): Boolean =
      if(c == '\n') true
      else if(c == '\r') {
        if(input.hasNext && input.head == '\n') input.next()
        true
      }
      else false


    def getc(): Boolean = if(input.hasNext) {
      val c = input.next()

      state match {
        case 0 =>
          if(isLineBreak(c)) {
            appendEntry(true)
            false
          }

          else {
            // Separator character: we've found a new entry in the current row.
            if(c == separator) appendEntry(true)

            // Escape character: if at the beginning of the cell, marks it as an escaped cell. Otherwise, treats it as
            // a normal character.
            else if(c == '"') {
              if(entry.isEmpty) {state = 1}
              else entry += c
            }

            // Whitespace is ignored if at the beginning of a non-escaped cell.
            else if(c.isWhitespace) {
              if(entry.nonEmpty) entry += c
            }


            // Regular character, appended to the current cell.
            else entry += c
            true
          }

        // - Escaped mode --------------------------------------------------------------------------------------------
        case 1 =>
          if(c == '"')            state = 2
          else if(isLineBreak(c)) entry += '\n'
          else                    entry += c

          true

        // - Ending escape mode --------------------------------------------------------------------------------------
        case 2 =>
          if(isLineBreak(c)) {
            appendEntry(false)
            wCount = 0
            state = 0
            false
          }
          // This means that 2 " characters were found in escape mode: that's an escaped ".
          else {
            if(c == '"' && wCount == 0) {
              entry += '"'
              wCount = 0
              state = 1
            }

            // End of escaped cell.
            else if(c == separator) {
              appendEntry(false)
              wCount = 0
              state = 0
            }
            else if(c.isWhitespace) wCount += 1
            else throw new IOException(s"Illegal CSV format: unexpected character '$c'")
            true
          }
      }
    }
    else if(state == 1) throw new IOException("Illegal CSV format: non-terminated escape sequence")
    else {
      if(entry.nonEmpty || row.nonEmpty) appendEntry(true)
      false
    }

    override def hasNext: Boolean = input.hasNext
    override def next(): ArrayBuffer[String] = {
      row.clear()

      while(getc()) {}

      row
    }
  }

  def unsafeRowsR(src: => Source, separator: Char): Process[Task, ArrayBuffer[String]] =
    io.resource(Task.delay(src))(src => Task.delay(src.close())) { src =>
      lazy val lines = new EntryIterator(src, separator)
      Task.delay { if(lines.hasNext) lines.next() else throw Cause.Terminated(Cause.End) }
    }

  def rowsR(src: => Source, separator: Char): Process[Task, Vector[String]] =
    unsafeRowsR(src, separator).map(_.toVector)
}
