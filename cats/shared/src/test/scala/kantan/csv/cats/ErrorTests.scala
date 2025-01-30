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

package kantan.csv.cats

import cats.Show
import cats.kernel.laws.discipline.EqTests
import kantan.csv.DecodeError
import kantan.csv.ParseError
import kantan.csv.ReadError
import kantan.csv.cats.arbitrary._
import kantan.csv.laws.discipline.DisciplineSuite

class ErrorTests extends DisciplineSuite {

  checkAll("ReadError", EqTests[ReadError].eqv)

  checkAll("DecodeError", EqTests[DecodeError].eqv)
  checkAll("DecodeError.OutOfbounds", EqTests[DecodeError.OutOfBounds].eqv)
  checkAll("DecodeError.TypeError", EqTests[DecodeError.TypeError].eqv)

  checkAll("ParseError", EqTests[ParseError].eqv)
  checkAll("ParseError.NoSuchElement", EqTests[ParseError.NoSuchElement.type].eqv)
  checkAll("ParseError.IOError", EqTests[ParseError.IOError].eqv)

  test("Show[DecodeError.OutOfBounds] should yield a string containing the expected index") {
    forAll { error: DecodeError.OutOfBounds =>
      Show[DecodeError.OutOfBounds].show(error) should include(error.index.toString)
      Show[DecodeError].show(error) should include(error.index.toString)
      Show[ReadError].show(error) should include(error.index.toString)
    }
  }

  test("Show[DecodeError.TypeError] should yield a string containing the expected message") {
    forAll { error: DecodeError.TypeError =>
      Show[DecodeError.TypeError].show(error) should include(error.message)
      Show[DecodeError].show(error) should include(error.message)
      Show[ReadError].show(error) should include(error.message)
    }
  }

  test("Show[ParseError.IOError] should yield a string containing the expected message") {
    forAll { error: ParseError.IOError =>
      Show[ParseError.IOError].show(error) should include(error.message)
      Show[ParseError].show(error) should include(error.message)
      Show[ReadError].show(error) should include(error.message)
    }
  }

  test("Show[ParseError.NoSuchElement] should yield a string containing 'trying to read from an empty reader'") {
    val expected = "trying to read from an empty reader"

    Show[ParseError.NoSuchElement.type].show(ParseError.NoSuchElement) should include(expected)
    Show[ParseError].show(ParseError.NoSuchElement) should include(expected)
    Show[ReadError].show(ParseError.NoSuchElement) should include(expected)
  }

}
