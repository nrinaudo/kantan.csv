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

package kantan.csv.ops

import kantan.codecs.Result
import kantan.codecs.laws.CodecValue
import kantan.csv._
import kantan.csv.laws._
import kantan.csv.laws.discipline.arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scala.util.Try

class CsvSourceOpsTests extends FunSuite with GeneratorDrivenPropertyChecks {
  def compare[F, A](csv: List[Result[F, A]], data: List[RowValue[A]]): Boolean = {
    val trimmed = data.filter(_.encoded.nonEmpty)

    if(csv.length != trimmed.length) false
    else csv.zip(trimmed).forall {
      case (Success(is), CodecValue.LegalValue(_, cs)) ⇒ is == cs
      case (Failure(_), CodecValue.IllegalValue(_))    ⇒ true
      case _                                           ⇒ false
    }
  }

  test("CsvSource instances should have a working asCsvReader method") {
    forAll { data: List[RowValue[List[Int]]] ⇒
      assert(compare(asCsv(data, ',').asCsvReader[List[Int]](',', false).toList, data))
    }
  }

  test("CsvSource instances should have a working readCsv method") {
    forAll { data: List[RowValue[List[Int]]] ⇒
      assert(compare(asCsv(data, ',').readCsv[List, List[Int]](',', false), data))
    }
  }

  def compareUnsafe[A](csv: ⇒ List[A], data: List[RowValue[A]]): Boolean = {
    def cmp(csv: List[A], data: List[RowValue[A]]): Boolean = (csv, data) match {
      case (Nil, Nil) ⇒ true
      case (h1 :: t1, CodecValue.LegalValue(_, h2) :: t2) if h1 == h2 ⇒ cmp(t1, t2)
      case _ ⇒ false
    }

    Try(csv) match {
      case scala.util.Success(is) ⇒ cmp(is, data)
      case _                      ⇒ data.exists {
        case CodecValue.IllegalValue(_) ⇒ true
        case _                          ⇒ false
      }
    }
  }

  test("CsvSource instances should have a working asUnsafeCsvReader method") {
    forAll { data: List[RowValue[List[Int]]] ⇒
      assert(compareUnsafe(asCsv(data, ',').asUnsafeCsvReader[List[Int]](',', false).toList, data))
    }
  }

  test("CsvSource instances should have a working unsafeReadCsv method") {
    forAll { data: List[RowValue[List[Int]]] ⇒
      assert(compareUnsafe(asCsv(data, ',').unsafeReadCsv[List, List[Int]](',', false), data))
    }
  }
}
