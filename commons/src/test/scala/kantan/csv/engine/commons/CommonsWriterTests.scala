package kantan.csv.engine.commons

import kantan.csv.laws.discipline.WriterEngineTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class CommonsWriterTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("CommonsWriter", WriterEngineTests(writer).writerEngine)
}
