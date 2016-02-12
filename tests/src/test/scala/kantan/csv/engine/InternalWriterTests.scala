package kantan.csv.engine

import kantan.csv.laws.discipline.WriterEngineTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class InternalWriterTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("InternalWriter", WriterEngineTests(WriterEngine.internal).writerEngine)
}