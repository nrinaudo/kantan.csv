package tabulate

import java.io._

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import tabulate.laws.discipline.arbitrary._
import tabulate.ops._

import scala.io.Codec

class CsvOutputTest extends FunSuite with GeneratorDrivenPropertyChecks {
  // None of the instances declared here are legal, but it'll do for our tests.

  implicit val writer = CsvOutput((w: Writer) => w)
  implicit val stringWriter = CsvOutput((w: StringWriter) => w)

  def validateByteArray(csv: List[List[String]])(implicit co: CsvOutput[ByteArrayOutputStream]) = {
    val out = new ByteArrayOutputStream()
    out.writeCsv[List[String]](csv, ',').close()
    assert(new String(out.toByteArray, Codec.UTF8.charSet) == csv.asCsv(','))
  }

  test("contramap should produce working CsvOutput instances") {
    implicit val stream: CsvOutput[ByteArrayOutputStream] = writer.contramap((o: OutputStream) => new OutputStreamWriter(o, Codec.UTF8.charSet))

    forAll(csv) { csv => validateByteArray(csv) }
  }

  test("Instances created from output streams should behave as expected") {
    implicit val stream = CsvOutput((o: ByteArrayOutputStream) => o)

    forAll(csv) { csv => validateByteArray(csv) }
  }

  test("CSV data should be correctly written (bit by bit)") {
    forAll(csv) { csv =>
      val out = new StringWriter()

      csv.foldLeft(out.asCsvWriter[List[String]](','))(_ write _).close()

      assert(out.toString == csv.asCsv(','))
    }
  }

  test("CSV data should be correctly written (in bulk)") {
    forAll(csv) { csv =>
      val out = new StringWriter()

      out.writeCsv(csv, ',')

      assert(out.toString == csv.asCsv(','))
    }
  }
}