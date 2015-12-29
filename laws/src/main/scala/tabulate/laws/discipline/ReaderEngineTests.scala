package tabulate.laws.discipline

import org.scalacheck.Prop
import tabulate.engine.ReaderEngine
import tabulate.laws.ReaderEngineLaws

trait ReaderEngineTests extends RfcReaderTests with SpectrumReaderTests with KnownFormatsReaderTests {
  def laws: ReaderEngineLaws

  def readerEngine: RuleSet = new RuleSet {
    def name: String = "readerEngine"
    def bases: Seq[(String, RuleSet)] = Nil
    def parents: Seq[RuleSet] = Seq(rfc4180, csvSpectrum, knownFormats)
    def props: Seq[(String, Prop)] = Seq.empty
  }
}

object ReaderEngineTests {
  def apply(engine: ReaderEngine): ReaderEngineTests = new ReaderEngineTests {
    override def laws: ReaderEngineLaws = ReaderEngineLaws(engine)
  }
}