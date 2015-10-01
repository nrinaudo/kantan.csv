package com.nrinaudo.csv

import org.scalatest.FunSuite
import com.nrinaudo.csv.ops._

class Rfc4180Suite extends FunSuite {
  def csvIs(data: String, expected: List[List[String]]): Unit =
    assert(data.asUnsafeCsvRows[List[String]](',', false).toList == expected)

  test("Each record is located on a separate line, delimited by a line break (CRLF)") {
    csvIs("aaa,bbb,ccc\r\nzzz,yyy,xxx\r\n", List(List("aaa", "bbb", "ccc"), List("zzz", "yyy", "xxx")))
  }

  test("The last record in the file may or may not have an ending line break.") {
    info("with line break")
    csvIs("aaa,bbb,ccc\r\nzzz,yyy,xxx\r\n", List(List("aaa", "bbb", "ccc"), List("zzz", "yyy", "xxx")))

    info("without line break")
    csvIs("aaa,bbb,ccc\r\nzzz,yyy,xxx", List(List("aaa", "bbb", "ccc"), List("zzz", "yyy", "xxx")))
  }

  test("Within the header and each record, there may be one or more fields, separated by commas.") {
    csvIs("aaa,bbb,ccc", List(List("aaa", "bbb", "ccc")))
  }

  test("Spaces are considered part of a field and should not be ignored.") {
    csvIs(" aaa\t,\tbbb , ccc\t", List(List(" aaa\t", "\tbbb ", " ccc\t")))
  }

  test("The last field in the record must not be followed by a comma.") {
    info("no trailing comma")
    csvIs("aaa,bbb,ccc", List(List("aaa", "bbb", "ccc")))

    info("trailing comma")
    csvIs("aaa,bbb,ccc,", List(List("aaa", "bbb", "ccc", "")))
  }

  test("Each field may or may not be enclosed in double quotes.") {
    info("with double quotes")
    csvIs("\"aaa\",\"bbb\",\"ccc\"\r\nzzz,yyy,xxx", List(List("aaa", "bbb", "ccc"), List("zzz", "yyy", "xxx")))

    info("without double quotes")
    csvIs("aaa,bbb,ccc\r\nzzz,yyy,xxx", List(List("aaa", "bbb", "ccc"), List("zzz", "yyy", "xxx")))
  }

  test("If fields are not enclosed with double quotes, then double quotes may not appear inside the fields.") {
    info("This constraint is relaxed to be compatible with more existing CSV implementations.")
    csvIs("aa\"a,bbb,ccc\r\nzzz,yyy,xxx", List(List("aa\"a", "bbb", "ccc"), List("zzz", "yyy", "xxx")))
  }

  test("Fields containing line breaks (CRLF), double quotes, and commas should be enclosed in double-quotes.") {
    info("line break")
    csvIs("\"aaa\",\"b\r\nbb\",\"ccc\"\r\nzzz,yyy,xxx", List(List("aaa", "b\nbb", "ccc"), List("zzz", "yyy", "xxx")))

    info("double quote")
    csvIs("\"aaa\",\"b\"\"bb\",\"ccc\"\r\nzzz,yyy,xxx", List(List("aaa", "b\"bb", "ccc"), List("zzz", "yyy", "xxx")))

    info("comma")
    csvIs("\"aaa\",\"b,bb\",\"ccc\"\r\nzzz,yyy,xxx", List(List("aaa", "b,bb", "ccc"), List("zzz", "yyy", "xxx")))
  }

  test("If double-quotes are used to enclose fields, then a double-quote appearing inside a field must be escaped by preceding it with another double quote.") {
    csvIs("\"aaa\",\"b\"\"bb\",\"ccc\"", List(List("aaa", "b\"bb", "ccc")))
  }
}
