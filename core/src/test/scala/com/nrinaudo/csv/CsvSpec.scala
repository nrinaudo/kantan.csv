package com.nrinaudo.csv

import org.scalatest.{FunSpec, Matchers}

import scala.io.{Codec, Source}


class CsvSpec  extends FunSpec with Matchers {
  implicit val codec = Codec.ISO8859

  def read(source: Source): List[List[String]] = rows[List[String]](source, ',').toList
  def readResource(str: String): List[List[String]] = read(Source.fromInputStream(getClass.getResourceAsStream(str)))
  def read(str: String): List[List[String]] = read(Source.fromString(str))


  describe("A CSV reader") {
    it("should return the content of a CSV stream") {
      read("""a,b,c
d,e,f
g,h,i""") should be (List(List("a", "b", "c"), List("d", "e", "f"), List("g", "h", "i")))
    }

    it("should not find any content in an empty stream") {
      read("") should be (List())
    }

    it("should trim non-escaped whitespace characters") {
      read("""  a  ,  b  ,  c
  d  ,  e  , f  """) should be (List(List("a", "b", "c"), List("d", "e", "f")))
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