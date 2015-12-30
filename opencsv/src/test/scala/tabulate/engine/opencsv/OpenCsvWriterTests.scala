package tabulate.engine.opencsv

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.WriterEngineTests

class OpenCsvWriterTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  checkAll("OpenCsvWriter", WriterEngineTests(engine).writerEngine)
}
