package com.nrinaudo.csv

import java.io.{PrintWriter, StringWriter}

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

import scala.io.{Codec, Source}

class CsvTest extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {
  implicit val codec = Codec.UTF8

  // CSV generators for property based testing.
  def cell: Gen[String] = Gen.nonEmptyListOf(Gen.choose(0x20.toChar, 0x7e.toChar)).map(_.mkString)
  def csv: Gen[List[List[String]]] = Gen.nonEmptyListOf(Gen.nonEmptyListOf(cell))


  // Helper functions to turn a CSV stream into a List[List[String]].
  def read(source: Source): List[List[String]] = rowsR[List[String]](source, ',').toList
  def readResource(str: String): List[List[String]] = read(Source.fromInputStream(getClass.getResourceAsStream(str)))
  def read(str: String): List[List[String]] = read(Source.fromString(str))

  // Helper function for writing CSV data to a string.
  def write(ss: List[List[String]]): String = {
    val sw = new StringWriter()
    val out = rowsW[List[String]](new PrintWriter(sw), ',')
    ss.foreach { s => out.write(s) }
    sw.toString
  }


  describe("A CSV reader") {
    it("should return the content of a CSV stream") {
      read("""a,b,c
d,e,f
g,h,i""") should be (List(List("a", "b", "c"), List("d", "e", "f"), List("g", "h", "i")))
    }

    it("should not find any content in an empty stream") {
      read("") should be (List())
    }

    it("should not trim non-escaped whitespace characters") {
      read("""  a  ,  b  ,  c
  d  ,  e  ,  f  """) should be (List(List("  a  ", "  b  ", "  c"), List("  d  ", "  e  ", "  f  ")))
    }

    it("should not trim escaped whitespace characters") {
      read("""a,"  b  ","  c  "
d,e,f""") should be (List(List("a", "  b  ", "  c  "), List("d", "e", "f")))
    }

    it("should ignore the last trailing empty line") {
      read("""a,b,c
d,e,f
""") should be (List(List("a", "b", "c"), List("d", "e", "f")))
    }

    it("should treat escaped line breaks as regular characters") {
      read("""a,b,"c
"
e,f,g""") should be(List(List("a", "b", "c\n"), List("e", "f", "g")))
    }

    it("should treat escaped \" as regular characters") {
      read("""a,b," ""c"" "
d,e,f""") should be (List(List("a", "b", " \"c\" "), List("d", "e", "f")))
    }

    it("should treat escaped separators as regular characters") {
      read("""a,b,",c,"
d,e,f""") should be (List(List("a", "b", ",c,"), List("d", "e", "f")))
    }

    it("should treat \" as regular characters outside of an escape sequence") {
      read("""a,b,a"c
d,e,f""") should be (List(List("a", "b", "a\"c"), List("d", "e", "f")))
    }

    it("should fail on unclosed escape sequences") {
      intercept[java.io.IOException] {
        read("""a,b,"c
d,e,f""")
      }
      ()
    }

    it("should fail on content after an escape sequence") {
      intercept[java.io.IOException] {
        read("""a,b,"c"   e
d,e,f""")
      }
      ()
    }

    it("should parse serialized CSV correctly") {
      forAll(csv) { ss: List[List[String]] => read(write(ss)) should be(ss) }
    }

    it("should work with all known formats") {
      val raw = readResource("/raw.csv")

      info("Excel for Mac")
      readResource("/excel_mac_12_0.csv") should be (raw)

      info("Numbers")
      readResource("/numbers_1_0_3.csv") should be (raw)

      info("Google Docs")
      readResource("/google_docs.csv") should be (raw)
    }
  }
}