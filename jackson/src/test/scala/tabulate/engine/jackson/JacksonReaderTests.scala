package tabulate.engine.jackson

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.ReaderEngineTests

class JacksonReaderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("JacksonReader", ReaderEngineTests(engine).readerEngine)
}
