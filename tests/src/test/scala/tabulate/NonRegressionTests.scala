package tabulate

import org.scalatest.FunSuite
import ops._

class NonRegressionTests extends FunSuite {
  test("cell with whitespace") {
    assert("abc, ".unsafeReadCsv[List, List[String]](',', false) == List(List("abc", " ")))
    assert("abc ,def".unsafeReadCsv[List, List[String]](',', false) == List(List("abc ", "def")))

    assert("abc, \n".unsafeReadCsv[List, List[String]](',', false) == List(List("abc", " ")))
    assert("abc ,def\n".unsafeReadCsv[List, List[String]](',', false) == List(List("abc ", "def")))
  }

  test("CRLF in escaped") {
    assert("1\r\n\"Once upon\r\na time\"".unsafeReadCsv[List, List[String]](',', false) == List(List("1"), List("Once upon\r\na time")))
  }
}
