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

import kantan.csv.CsvConfiguration.{Header, QuotePolicy}

/** Configuration for how to read / write CSV data.
  *
  * Note that all engines don't necessarily support all features.
  */
final case class CsvConfiguration(columnSeparator: Char, quote: Char, quotePolicy: QuotePolicy, header: Header) {
  /** Use the specified quote character. */
  def withQuote(char: Char): CsvConfiguration = copy(quote = char)

  /** Quote all cells, whether they need it or not. */
  def quoteAll: CsvConfiguration = withQuotePolicy(QuotePolicy.Always)
  /** Quote only cells that need it. */
  def quoteWhenNeeded: CsvConfiguration = withQuotePolicy(QuotePolicy.WhenNeeded)
  /** Use the specified quoting policy. */
  def withQuotePolicy(policy: QuotePolicy): CsvConfiguration = copy(quotePolicy = policy)

  /** Use the specified character for column separator. */
  def withColumnSeparator(char: Char): CsvConfiguration = copy(columnSeparator = char)

  /** Use the specified header configuration. */
  def withHeader(header: CsvConfiguration.Header): CsvConfiguration = copy(header = header)
  /** Expect a header when reading, use the specified sequence when writing. */
  def withHeader(ss: String*): CsvConfiguration = withHeader(CsvConfiguration.Header.Explicit(ss))
  /** If `flag` is `true`, expect a header when reading. Otherwise, don't. */
  def withHeader(flag: Boolean): CsvConfiguration = if(flag) withHeader else withoutHeader
  /** Expect a header when reading, do not use one when writing. */
  def withHeader: CsvConfiguration = withHeader(CsvConfiguration.Header.Implicit)
  /** Do not use a header, either when reading or writing. */
  def withoutHeader: CsvConfiguration = withHeader(CsvConfiguration.Header.None)
  /** Checks whether this configuration has a header, either for reading or writing. */
  def hasHeader: Boolean = header != CsvConfiguration.Header.None

  // TODO: remove when we drop support for 2.10
  // Override the default implementation to prevent compilation failures under 2.10.6.
  override def hashCode: Int = {
    import scala.runtime.Statics
    var acc: Int = -889275714
    acc = Statics.mix(acc, columnSeparator.toInt)
    acc = Statics.mix(acc, quote.toInt)
    acc = Statics.mix(acc, quotePolicy.hashCode())
    acc = Statics.mix(acc, header.hashCode())
    Statics.finalizeHash(acc, 3)
  }

  // TODO: remove when we drop support for 2.10
  override def equals(obj: Any): Boolean = obj match {
    case CsvConfiguration(cs, q, p, ss) ⇒ cs == columnSeparator && q == quote && ss == header && p == quotePolicy
    case _                              ⇒ false
  }
}

object CsvConfiguration {
  val rfc: CsvConfiguration = CsvConfiguration(',', '"', QuotePolicy.WhenNeeded, Header.None)

  sealed abstract class QuotePolicy extends Product with Serializable
  object QuotePolicy {
    case object Always extends QuotePolicy
    case object WhenNeeded extends QuotePolicy
  }


  /** Various possible CSV header configurations. */
  sealed abstract class Header extends Product with Serializable
  object Header {
    /** No header defined. */
    case object None extends Header

    /** Use a header when possible.
      *
      * When decoding, the first row will always be treated as a header.
      *
      * When encoding, use a header if one is available through [[HeaderEncoder]], but skip it otherwise.
      */
    case object Implicit extends Header

    /** Use the specified header.
      *
      * This is equivalent to [[Implicit]] when decoding. When encoding, it takes precedence over whatever header
      * might have been defined through a [[HeaderEncoder]] instance.
      */
    final case class Explicit(header: Seq[String]) extends Header
  }
}
