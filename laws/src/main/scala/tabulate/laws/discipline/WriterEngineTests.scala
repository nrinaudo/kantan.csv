package tabulate.laws.discipline

import org.scalacheck.Prop._
import org.typelevel.discipline.Laws
import tabulate.engine.WriterEngine
import tabulate.laws.WriterEngineLaws

trait WriterEngineTests extends Laws {
  def laws: WriterEngineLaws

  def writerEngine: RuleSet = new DefaultRuleSet(
    name = "writerEngine",
    parent = None,
    "round-trip"                 → forAll(laws.roundTrip _),
    "no trailing cell separator" → forAll(laws.noTrailingSeparator _),
    "crlf row separator"         → forAll(laws.crlfAsRowSeparator _)
  )
}

object WriterEngineTests {
  def apply(engine: WriterEngine): WriterEngineTests = new WriterEngineTests {
    override def laws: WriterEngineLaws = WriterEngineLaws(engine)
  }
}