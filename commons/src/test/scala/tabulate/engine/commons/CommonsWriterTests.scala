package tabulate.engine.commons

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.WriterEngineTests

class CommonsWriterTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CommonsWriter", WriterEngineTests(engine).writerEngine)
}
