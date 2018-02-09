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

import org.scalatest.{FunSuite, Matchers}
import org.scalatest.EitherValues._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

// Shapeless' Lazy macros generate code that contain null.
@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class ParseResultTests extends FunSuite with GeneratorDrivenPropertyChecks with Matchers {
  test("ParseResult.success should return a Right") {
    forAll { i: Int ⇒
      ParseResult.success(i).right.value should be(i)
    }
  }

  test("ParseResult.apply should return a Right on 'good' values") {
    forAll { i: Int ⇒
      ParseResult(i).right.value should be(i)
    }
  }

  test("ParseResult.apply should return a Left on 'bad' values") {
    forAll { e: Exception ⇒
      ParseResult(throw e).left.value should be(ParseError.IOError(e))
    }
  }
}
