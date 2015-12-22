package tabulate

import org.scalatest.FunSuite
import ops._

class NonRegressionTests extends FunSuite {
  test("cell with whitespace") {
    //assert("abc, \n".asUnsafeCsvRows[List[String]](',', false).toList == List(List("abc", " ")))
  }

  test("CRLF in escaped") {
    assert("1\r\n\"Once upon\r\na time\"".asUnsafeCsvRows[List[String]](',', false).toList == List(List("1"), List("Once upon\r\na time")))
  }
}
