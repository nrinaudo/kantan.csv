/*
 * Copyright 2015 Nicolas Rinaudo
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
final case class CsvConfiguration(cellSeparator: Char, quote: Char, quotePolicy: QuotePolicy, header: Header) {

  /** Use the specified quote character. */
  def withQuote(char: Char): CsvConfiguration = copy(quote = char)

  /** Quote all cells, whether they need it or not. */
  def quoteAll: CsvConfiguration = withQuotePolicy(QuotePolicy.Always)

  /** Quote only cells that need it. */
  def quoteWhenNeeded: CsvConfiguration = withQuotePolicy(QuotePolicy.WhenNeeded)

  /** Use the specified quoting policy. */
  def withQuotePolicy(policy: QuotePolicy): CsvConfiguration = copy(quotePolicy = policy)

  /** Use the specified character for cell separator. */
  def withCellSeparator(char: Char): CsvConfiguration = copy(cellSeparator = char)

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
}

object CsvConfiguration {
  val rfc: CsvConfiguration = CsvConfiguration(',', '"', QuotePolicy.WhenNeeded, Header.None)

  sealed abstract class QuotePolicy extends Product with Serializable
  object QuotePolicy {
    case object Always     extends QuotePolicy
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
