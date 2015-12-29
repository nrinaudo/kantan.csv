package tabulate

import org.scalatest.FunSuite
import ops._

class NonRegressionTests extends FunSuite {
  test("cell with whitespace") {
    assert("abc, ".unsafeReadCsv[List[String], List](',', false) == List(List("abc", " ")))
    assert("abc ,def".unsafeReadCsv[List[String], List](',', false) == List(List("abc ", "def")))

    assert("abc, \n".unsafeReadCsv[List[String], List](',', false) == List(List("abc", " ")))
    assert("abc ,def\n".unsafeReadCsv[List[String], List](',', false) == List(List("abc ", "def")))
  }

  test("CRLF in escaped") {
    assert("1\r\n\"Once upon\r\na time\"".unsafeReadCsv[List[String], List](',', false) == List(List("1"), List("Once upon\r\na time")))
  }
}
