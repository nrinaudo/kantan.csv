package kantan.csv.engine.commons

import kantan.csv.laws.discipline.ReaderEngineTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class CommonsReaderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CommonsReader", ReaderEngineTests(engine).readerEngine)
}
