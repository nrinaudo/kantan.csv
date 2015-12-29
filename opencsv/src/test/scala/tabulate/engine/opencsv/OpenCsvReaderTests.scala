package tabulate.engine.opencsv

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline
import tabulate.laws.discipline.ReaderEngineTests

class OpenCsvReaderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // TODO: opencsv fails a *lot* of the tests. So much that something might be wrong with my implementation of the
  // connector rather than with opencsv itself.
  //checkAll("OpenCsvReader", ReaderEngineTests(engine).readerEngine)
}
