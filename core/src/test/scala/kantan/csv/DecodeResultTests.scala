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
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class DecodeResultTests extends FunSuite with GeneratorDrivenPropertyChecks {
  test("DecodeResult.success should return a success") {
    forAll { i: Int ⇒ assert(DecodeResult.success(i) == Success(i)) }
  }

  test("DecodeResult.apply should return a success on 'good' values") {
    forAll { i: Int ⇒ assert(DecodeResult(i) == Success(i)) }
  }

  test("DecodeResult.apply should return a failure on 'bad' values") {
    forAll { e: Exception ⇒ assert(DecodeResult(throw e) == Failure(DecodeError.TypeError(e))) }
  }

  test("DecodeResult.outOfBounds should return a failure ") {
    forAll { i: Int ⇒ assert(DecodeResult.outOfBounds(i) == Failure(DecodeError.OutOfBounds(i))) }
  }
}
