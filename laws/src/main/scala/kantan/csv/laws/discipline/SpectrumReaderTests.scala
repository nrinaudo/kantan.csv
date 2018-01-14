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
package laws
package discipline

import org.scalacheck.Prop
import org.typelevel.discipline.Laws

trait SpectrumReaderTests extends Laws {
  def laws: SpectrumReaderLaws

  def csvSpectrum: RuleSet = new DefaultRuleSet(
    name = "csvSpectrum",
    parent = None,
    "comma in quotes"     → Prop(laws.commaInQuotes),
    "empty"               → Prop(laws.empty),
    "empty crlf"          → Prop(laws.emptyCRLF),
    "escaped quotes"      → Prop(laws.escapedQuotes),
    "json"                → Prop(laws.json),
    "newlines"            → Prop(laws.newLines),
    "newlines crlf"       → Prop(laws.newLinesCRLF),
    "quotes and newlines" → Prop(laws.quotesAndNewLines),
    "simple"              → Prop(laws.simple),
    "simple crlf"         → Prop(laws.simpleCRLF),
    "utf8"                → Prop(laws.utf8)
  )
}
