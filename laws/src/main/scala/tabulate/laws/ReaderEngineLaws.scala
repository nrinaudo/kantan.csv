package tabulate.laws

import tabulate.engine.ReaderEngine

trait ReaderEngineLaws extends RfcReaderLaws with SpectrumReaderLaws with KnownFormatsReaderLaws

object ReaderEngineLaws {
  def apply(e: ReaderEngine): ReaderEngineLaws = new ReaderEngineLaws {
    override implicit val engine = e
  }
}