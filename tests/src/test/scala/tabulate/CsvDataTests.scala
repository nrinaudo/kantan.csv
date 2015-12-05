package tabulate

import java.nio.charset.Charset

import ops._
import java.io.{StringReader, ByteArrayInputStream, StringWriter}

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import tabulate.laws.discipline.arbitrary._

import scala.io.Source

class CsvDataTests extends FunSuite with GeneratorDrivenPropertyChecks {
  private def write(csv: List[List[String]]): String = {
    val out = new StringWriter
    csv.foldLeft(out.asCsvWriter[List[String]](','))(_ write _).close()
    out.toString
  }

  test("CsvData created from a Source should behave as expected") {
    forAll(csv) { csv =>
      val data = Source.fromString(write(csv))

      assert(CsvData(data).asRows[List[String]](',', false).map(_.get).toList == csv)
    }
  }

  test("CsvData created from an InputStream should behave as expected") {
    forAll(csv) { csv =>
      val data = new ByteArrayInputStream(write(csv).getBytes(Charset.forName("UTF-8")))

      assert(CsvData(data).asRows[List[String]](',', false).map(_.get).toList == csv)
    }
  }

  test("CsvData created from a Reader should behave as expected") {
    forAll(csv) { csv =>
      val data = new StringReader(write(csv))

      assert(CsvData(data).asRows[List[String]](',', false).map(_.get).toList == csv)
    }
  }
}
