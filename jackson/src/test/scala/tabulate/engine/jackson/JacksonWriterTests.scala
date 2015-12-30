package tabulate.engine.jackson

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.WriterEngineTests

class JacksonWriterTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("JacksonWriter", WriterEngineTests(engine).writerEngine)
}
