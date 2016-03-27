package kantan.csv.engine.opencsv

import kantan.csv.laws.discipline.WriterEngineTests
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class OpenCsvWriterTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("OpenCsvWriter", WriterEngineTests(writer).writerEngine)
}
