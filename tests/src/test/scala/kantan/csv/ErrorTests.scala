/*
 * Copyright 2016 Nicolas Rinaudo
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
import kantan.csv.DecodeError.TypeError
import kantan.csv.ParseError.IOError
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ErrorTests extends FunSuite with GeneratorDrivenPropertyChecks {
  test("TypeErrors should be equal if the underlying exceptions have the same class") {
    forAll { (e1: TypeError, e2: ReadError) ⇒
      assert((e1 == e2) == ((e1, e2) match {
        case (TypeError(t1), TypeError(t2)) ⇒ t1.getClass == t2.getClass
        case _                              ⇒ false
      }))
    }
  }

  test("TypeErrors should have identical hashCodes if the underlying exceptions have the same class") {
    forAll { (e1: TypeError, e2: ReadError) ⇒
      assert((e1.hashCode() == e2.hashCode()) == ((e1, e2) match {
        case (TypeError(t1), TypeError(t2)) ⇒ t1.getClass == t2.getClass
        case _                              ⇒ false
      }))
    }
  }

  test("IOErrors should be equal if the underlying exceptions have the same class") {
    forAll { (e1: IOError, e2: ReadError) ⇒
      assert((e1 == e2) == ((e1, e2) match {
        case (IOError(t1), IOError(t2)) ⇒ t1.getClass == t2.getClass
        case _                            ⇒ false
      }))
    }
  }

  test("IOErrors should have identical hashCodes if the underlying exceptions have the same class") {
    forAll { (e1: IOError, e2: ReadError) ⇒
      assert((e1.hashCode() == e2.hashCode()) == ((e1, e2) match {
        case (IOError(t1), IOError(t2)) ⇒ t1.getClass == t2.getClass
        case _                            ⇒ false
      }))
    }
  }
}
