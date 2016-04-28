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

import kantan.csv.DecodeError.TypeError
import kantan.csv.ParseError.IOError
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ErrorTests extends FunSuite with GeneratorDrivenPropertyChecks {
  test("TypeError") {
    forAll { (t1: Throwable, t2: Throwable) ⇒
      assert((TypeError(t1) == TypeError(t2)) == (t1.getClass == t2.getClass))
      assert((TypeError(t1).hashCode() == TypeError(t2).hashCode()) == (t1.getClass == t2.getClass))
    }
  }

  test("IOError") {
    forAll { (t1: Throwable, t2: Throwable) ⇒
      assert((IOError(t1) == IOError(t2)) == (t1.getClass == t2.getClass))
      assert((IOError(t1).hashCode() == IOError(t2).hashCode()) == (t1.getClass == t2.getClass))
    }
  }
}
