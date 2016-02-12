package kantan.csv.engine

import kantan.csv.laws.discipline.ReaderEngineTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class InternalReaderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("InternalReader", ReaderEngineTests(ReaderEngine.internal).readerEngine)
}
