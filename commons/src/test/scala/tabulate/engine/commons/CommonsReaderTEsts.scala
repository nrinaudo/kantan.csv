package tabulate.engine.commons

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.ReaderEngineTests

class CommonsReaderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CommonsReader", ReaderEngineTests(engine).readerEngine)
}
