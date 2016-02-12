package kantan.csv.laws.discipline

import kantan.csv.engine.WriterEngine
import kantan.csv.laws.WriterEngineLaws
import org.scalacheck.Prop._
import org.typelevel.discipline.Laws

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