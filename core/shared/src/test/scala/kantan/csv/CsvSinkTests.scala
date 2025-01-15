/*
 * Copyright 2015 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.csv

import kantan.csv.laws.discipline.arbitrary._
import kantan.csv.ops._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

import java.io.ByteArrayOutputStream
import java.io.StringWriter
import scala.io.Codec

class CsvSinkTests extends AnyFunSuite with ScalaCheckPropertyChecks with Matchers {
  test("CSV data should be correctly written to an output stream (bit by bit)") {
    forAll(csv) { csv =>
      val out = new ByteArrayOutputStream()

      csv.foldLeft(out.asCsvWriter[List[String]](rfc))(_ write _).close()

      new String(out.toByteArray, Codec.UTF8.charSet) should be(csv.asCsv(rfc))
    }
  }

  test("CSV data should be correctly written to an output stream (in bulk)") {
    forAll(csv) { csv =>
      val out = new ByteArrayOutputStream()

      out.writeCsv(csv, rfc)

      new String(out.toByteArray, Codec.UTF8.charSet) should be(csv.asCsv(rfc))
    }
  }

  test("CSV data should be correctly written to a writer (bit by bit)") {
    forAll(csv) { csv =>
      val out = new StringWriter()

      csv.foldLeft(out.asCsvWriter[List[String]](rfc))(_ write _).close()

      out.toString should be(csv.asCsv(rfc))
    }
  }

  test("CSV data should be correctly written to a writer (in bulk)") {
    forAll(csv) { csv =>
      val out = new StringWriter()

      out.writeCsv(csv, rfc)

      out.toString should be(csv.asCsv(rfc))
    }
  }
}
