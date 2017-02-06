/*
 * Copyright 2017 Nicolas Rinaudo
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

package kantan.csv.ops

import kantan.csv._
import kantan.csv.laws._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class CsvReaderOpsTests extends FunSuite with GeneratorDrivenPropertyChecks {
  def asCsvReader[A: RowDecoder](data: List[RowValue[A]]): CsvReader[ReadResult[A]] =
    asCsv(data, ',').asCsvReader[A](',', false)

  test("CsvReader[ReadResult] instances should have a working mapResult method") {
    forAll { (data: List[RowValue[List[Int]]], f: List[Int] ⇒ List[Float]) ⇒
      assert(asCsvReader(data).mapResult(f).toList == asCsvReader(data).map(_.map(f)).toList)
    }
  }

  test("CsvReader[ReadResult] instances should have a working flatMapResult method") {
    forAll { (data: List[RowValue[List[Int]]], f: List[Int] ⇒ ReadResult[List[Float]]) ⇒
      assert(asCsvReader(data).flatMapResult(f).toList == asCsvReader(data).map(_.flatMap(f)).toList)
    }
  }

  test("CsvReader[ReadResult] instances should have a working filter method") {
    forAll { (data: List[RowValue[List[Int]]], f: List[Int] ⇒ Boolean) ⇒
      assert(asCsvReader(data).filterResult(f).toList == asCsvReader(data).filter(_.exists(f)).toList)
    }
  }
}
