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

package kantan.csv.scalaz

import kantan.codecs.scalaz.laws.discipline.ScalazDisciplineSuite
import kantan.csv.DecodeError
import kantan.csv.ParseError
import kantan.csv.ReadError
import kantan.csv.scalaz.arbitrary._
import scalaz.Show
import scalaz.scalacheck.ScalazProperties.{equal => equ}

class ErrorTests extends ScalazDisciplineSuite {

  checkAll("ReadError", equ.laws[ReadError])

  checkAll("DecodeError", equ.laws[DecodeError])
  checkAll("DecodeError.OutOfbounds", equ.laws[DecodeError.OutOfBounds])
  checkAll("DecodeError.TypeError", equ.laws[DecodeError.TypeError])

  checkAll("ParseError", equ.laws[ParseError])
  checkAll("ParseError.NoSuchElement", equ.laws[ParseError.NoSuchElement.type])
  checkAll("ParseError.IOError", equ.laws[ParseError.IOError])

  test("Show[DecodeError.OutOfBounds] should yield a string containing the expected index") {
    forAll { error: DecodeError.OutOfBounds =>
      Show[DecodeError.OutOfBounds].shows(error) should include(error.index.toString)
      Show[DecodeError].shows(error) should include(error.index.toString)
      Show[ReadError].shows(error) should include(error.index.toString)
    }
  }

  test("Show[DecodeError.TypeError] should yield a string containing the expected message") {
    forAll { error: DecodeError.TypeError =>
      Show[DecodeError.TypeError].shows(error) should include(error.message)
      Show[DecodeError].shows(error) should include(error.message)
      Show[ReadError].shows(error) should include(error.message)
    }
  }

  test("Show[ParseError.IOError] should yield a string containing the expected message") {
    forAll { error: ParseError.IOError =>
      Show[ParseError.IOError].shows(error) should include(error.message)
      Show[ParseError].shows(error) should include(error.message)
      Show[ReadError].shows(error) should include(error.message)
    }
  }

  test("Show[ParseError.NoSuchElement] should yield a string containing 'trying to read from an empty reader'") {
    val expected = "trying to read from an empty reader"

    Show[ParseError.NoSuchElement.type].shows(ParseError.NoSuchElement) should include(expected)
    Show[ParseError].shows(ParseError.NoSuchElement) should include(expected)
    Show[ReadError].shows(ParseError.NoSuchElement) should include(expected)
  }

}
