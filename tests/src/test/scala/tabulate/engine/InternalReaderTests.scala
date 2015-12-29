package tabulate.engine

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.ReaderEngineTests

class InternalReaderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("InternalReader", ReaderEngineTests(ReaderEngine.internal).readerEngine)
}
