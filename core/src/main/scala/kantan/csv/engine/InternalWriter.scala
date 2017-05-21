/*
 * Copyright 2017 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.csv.engine

import java.io.Writer
import kantan.csv.{CsvConfiguration, CsvWriter}
import scala.annotation.tailrec

private[csv] class InternalWriter(private val out: Writer, val conf: CsvConfiguration) extends CsvWriter[Seq[String]] {

  private def safeWrite(str: String): Unit = {
    @tailrec
    def escape(mark: Int, i: Int): Unit =
      if(i >= str.length) {
        if(mark != i) out.write(str, mark, i - mark)
      }
      else if(str.charAt(i) == conf.quote) {
        out.write(str, mark, i - mark + 1)
        out.write(conf.quote.toInt)
        escape(i + 1, i + 1)
      }
      else escape(mark, i + 1)

    @tailrec
    def escapeIndex(index: Int): Int =
      if(index >= str.length) -1
      else {
        val c = str.charAt(index)
        if(c == conf.quote || c == conf.columnSeparator || c == '\n' || c == '\r') index
        else escapeIndex(index + 1)
      }

    // If we're configured to always quote, do so.
    if(conf.quotePolicy == CsvConfiguration.QuotePolicy.Always) {
      out.write(conf.quote.toInt)
      out.write(str)
      out.write(conf.quote.toInt)
    }

    // Otherwise, only quotes when needed.
    else {
      val index = escapeIndex(0)

      if(index == -1) out.write(str)
      else {
        out.write(conf.quote.toInt)
        out.write(str, 0, index)
        escape(index, index)
        out.write(conf.quote.toInt)
      }
    }
  }

  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  override def write(ss: Seq[String]): CsvWriter[Seq[String]] = {
    var first = true
    for(s ← ss) {
      if(first) first = false
      else      out.write(conf.columnSeparator.toInt)
      safeWrite(s)
    }
    /*if(!first)*/ out.write("\r\n") // According to the RFC, \n alone is not valid.
    this
  }

  override def close(): Unit = out.close()
}
