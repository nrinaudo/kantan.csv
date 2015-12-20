package tabulate

import org.scalatest.FunSuite
import ops._

class NonRegressionTests extends FunSuite {
  test("cell with whitespace") {
    assert("abc, \n".asUnsafeCsvRows[List[String]](',', false).toList == List(List("abc", " ")))
  }
}
