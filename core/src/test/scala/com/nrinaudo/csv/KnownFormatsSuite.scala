package com.nrinaudo.csv

import org.scalatest.FunSuite
import com.nrinaudo.csv.tools._

class KnownFormatsSuite extends FunSuite {
  test("All known formats must be supported") {
    val raw = readResource("/raw.csv")

    info("Excel for Mac")
    assert(readResource("/excel_mac_12_0.csv") == raw)

    info("Numbers")
    assert(readResource("/numbers_1_0_3.csv") == raw)

    info("Google Docs")
    assert(readResource("/google_docs.csv") == raw)
  }
}
