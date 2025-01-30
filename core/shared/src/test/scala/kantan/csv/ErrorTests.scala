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

import kantan.csv.DecodeError.TypeError
import kantan.csv.ParseError.IOError
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class ErrorTests extends AnyFunSuite with ScalaCheckPropertyChecks with Matchers {
  test("TypeErrors should be equal if the underlying errors are the same") {
    forAll { (e1: TypeError, e2: ReadError) =>
      (e1, e2) match {
        case (TypeError(t1), TypeError(t2)) => (e1 == e2) should be(t1 == t2)
        case _                              => e1 should not be e2
      }
    }
  }

  test("TypeErrors should have identical hashCodes if the underlying errors have identical hashCodes") {
    forAll { (e1: TypeError, e2: TypeError) =>
      (e1.hashCode() == e2.hashCode()) should be(e1.message.hashCode == e2.message.hashCode)
    }
  }

  test("IOErrors should be equal if the underlying errors are the same") {
    forAll { (e1: IOError, e2: ReadError) =>
      (e1, e2) match {
        case (IOError(t1), IOError(t2)) => (e1 == e2) should be(t1 == t2)
        case _                          => e1 should not be e2
      }
    }
  }

  test("IOErrors should have identical hashCodes if the underlying errors have identical hashCodes") {
    forAll { (e1: IOError, e2: IOError) =>
      (e1.hashCode() == e2.hashCode()) should be(e1.message.hashCode == e2.message.hashCode)
    }
  }
}
