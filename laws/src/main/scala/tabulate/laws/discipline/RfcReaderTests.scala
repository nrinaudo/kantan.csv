package tabulate.laws.discipline

import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import tabulate.laws.RfcReaderLaws

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
