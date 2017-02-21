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

package kantan.csv

final case class CsvConfiguration(columnSeparator: Char, quote: Char, header: Seq[String]) {
  def withQuote(char: Char): CsvConfiguration = copy(quote = char)
  def withColumnSeparator(char: Char): CsvConfiguration = copy(columnSeparator = char)

  def withHeader(ss: String*): CsvConfiguration = copy(header = ss)
  def withHeader(flag: Boolean): CsvConfiguration = if(flag) withHeader else withoutHeader
  def withHeader: CsvConfiguration = copy(header = Seq(""))
  def withoutHeader: CsvConfiguration = copy(header = Seq.empty[String])
  def hasHeader: Boolean = header.nonEmpty

  // Override the default implementation to prevent compilation failures under 2.10.6.
  override def hashCode: Int = {
    import scala.runtime.Statics
    var acc: Int = -889275714
    acc = Statics.mix(acc, columnSeparator.toInt)
    acc = Statics.mix(acc, quote.toInt)
    acc = Statics.mix(acc, header.hashCode())
    Statics.finalizeHash(acc, 3)
  }

  override def equals(obj: Any): Boolean = obj match {
    case CsvConfiguration(cs, q, ss) ⇒ cs == columnSeparator && q == quote && ss == header
    case _                           ⇒ false
  }
}
