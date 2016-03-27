package kantan.csv.engine.jackson

import kantan.csv.laws.discipline.WriterEngineTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class JacksonWriterTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("JacksonWriter", WriterEngineTests(writer).writerEngine)
}
