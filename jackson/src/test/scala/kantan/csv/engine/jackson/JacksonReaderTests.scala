package kantan.csv.engine.jackson

import kantan.csv.laws.discipline.ReaderEngineTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class JacksonReaderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("JacksonReader", ReaderEngineTests(reader).readerEngine)
}
