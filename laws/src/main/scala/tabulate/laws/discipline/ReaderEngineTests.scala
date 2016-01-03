package tabulate.laws.discipline

import org.scalacheck.Prop
import org.scalacheck.Prop._
import tabulate.engine.ReaderEngine
import tabulate.laws.ReaderEngineLaws

trait ReaderEngineTests extends RfcReaderTests with SpectrumReaderTests with KnownFormatsReaderTests {
  def laws: ReaderEngineLaws

  def readerEngine: RuleSet = new RuleSet {
    def name: String = "readerEngine"
    def bases: Seq[(String, RuleSet)] = Nil
    def parents: Seq[RuleSet] = Seq(rfc4180, csvSpectrum, knownFormats)
    def props: Seq[(String, Prop)] = Seq(
      "drop"               -> forAll(laws.drop _),
      "dropWhile"          -> forAll(laws.dropWhile _),
      "take"               -> forAll(laws.take _),
      "forall"             -> forAll(laws.forall _),
      "find"               -> forAll(laws.find _),
      "exists"             -> forAll(laws.exists _),
      "filter"             -> forAll(laws.filter _),
      "withFilter"         -> forAll(laws.withFilter _),
      "toStream"           -> forAll(laws.toStream _),
      "toTraversable"      -> forAll(laws.toTraversable _),
      "toIterator"         -> forAll(laws.toIterator _),
      "hasDefiniteSize"    -> forAll(laws.toStream _),
      "isEmpty"            -> forAll(laws.toTraversable _),
      "isTraversableAgain" -> forAll(laws.isTraversableAgain _)
    )
  }
}

object ReaderEngineTests {
  def apply(engine: ReaderEngine): ReaderEngineTests = new ReaderEngineTests {
    override def laws: ReaderEngineLaws = ReaderEngineLaws(engine)
  }
}