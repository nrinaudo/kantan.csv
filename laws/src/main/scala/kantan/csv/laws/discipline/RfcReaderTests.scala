/*
 * Copyright 2016 Nicolas Rinaudo
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

package kantan.csv.laws.discipline

import kantan.csv.laws.RfcReaderLaws
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

trait RfcReaderTests extends Laws {
  def laws: RfcReaderLaws

  def rfc4180: RuleSet = new DefaultRuleSet(
    name = "rfc4180",
    parent = None,
    "crlf row separator"        → forAll(laws.crlfRowSeparator _),
    "lf row separator"          → forAll(laws.lfRowSeparator _),
    "crlf ending"               → forAll(laws.crlfEnding _),
    "lf ending"                 → forAll(laws.lfEnding _),
    "empty ending"              → forAll(laws.emptyEnding _),
    "leading whitespace"        → forAll(laws.leadingWhitespace _),
    "trailing whitespace"       → forAll(laws.trailingWhitespace _),
    "trailing comma"            → forAll(laws.trailingWhitespace _),
    "unnecessary double quotes" → forAll(laws.unnecessaryDoubleQuotes _),
    "unescaped double quotes"   → forAll(laws.unescapedDoubleQuotes _),
    "escaped content"           → forAll(laws.escapedCells _)
  )
}
