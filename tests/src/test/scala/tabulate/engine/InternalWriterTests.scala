package tabulate.engine

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.WriterEngineTests

class InternalWriterTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("InternalWriter", WriterEngineTests(WriterEngine.internal).writerEngine)
}