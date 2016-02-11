package tabulate.laws.discipline

import org.scalacheck.Prop
import org.typelevel.discipline.Laws
import tabulate.engine.ReaderEngine
import tabulate.laws.KnownFormatsReaderLaws

trait KnownFormatsReaderTests extends Laws {
  def laws: KnownFormatsReaderLaws

    def knownFormats: RuleSet = new DefaultRuleSet(
      name = "knownFormats",
      parent = None,
      "excel for mac 12.0"  → Prop(laws.excelMac12_0),
      "numbers 1.0.3"       → Prop(laws.numbers1_0_3),
      "google docs"         → Prop(laws.googleDocs)
    )
}
