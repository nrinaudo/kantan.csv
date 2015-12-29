package tabulate.laws.discipline

import org.scalacheck.Prop
import org.typelevel.discipline.Laws
import tabulate.engine.ReaderEngine
import tabulate.laws.SpectrumReaderLaws

trait SpectrumReaderTests extends Laws {
  def laws: SpectrumReaderLaws

  def csvSpectrum: RuleSet = new DefaultRuleSet(
    name = "csvSpectrum",
    parent = None,
    "comma in quotes"     -> Prop(laws.commaInQuotes),
    "empty"               -> Prop(laws.empty),
    "empty crlf"          -> Prop(laws.emptyCRLF),
    "escaped quotes"      -> Prop(laws.escapedQuotes),
    "json"                -> Prop(laws.json),
    "newlines"            -> Prop(laws.newLines),
    "newlines crlf"       -> Prop(laws.newLinesCRLF),
    "quotes and newlines" -> Prop(laws.quotesAndNewLines),
    "simple"              -> Prop(laws.simple),
    "simple crlf"         -> Prop(laws.simpleCRLF),
    "utf8"                -> Prop(laws.utf8)
  )
}

object SpectrumReaderTests {
  def apply(engine: ReaderEngine): SpectrumReaderTests = new SpectrumReaderTests {
    override def laws: SpectrumReaderLaws = SpectrumReaderLaws(engine)
  }
}