package tabulate

import org.scalatest.FunSuite
import tabulate.ops._
import scala.io._
import argonaut._, Argonaut._

class CsvSpectrumTests extends FunSuite {
  val files = List("comma_in_quotes", "empty", "empty_crlf", "escaped_quotes", "json", "newlines", "newlines_crlf",
    "quotes_and_newlines", "simple", "simple_crlf", "utf8")

  def csvAsMap(name: String): List[Map[String, String]] = {
    val (h :: b) = getClass.getResource(s"/csv-spectrum/csvs/$name.csv").asUnsafeCsvRows[List[String]](',', false).toList

    b.map(row => h.zip(row).foldLeft(Map.empty[String, String])((acc, tuple) => acc + tuple))
  }

  def jsonAsMap(name: String): List[Map[String, String]] = {
    Source.fromURL(getClass.getResource(s"/csv-spectrum/json/$name.json")).mkString
      .decodeOption[List[Map[String, String]]].get
  }

  files.foreach { file =>
    test(s"$file should yield the expected results") {
      assert(csvAsMap(file) == jsonAsMap(file))
    }
  }
}
