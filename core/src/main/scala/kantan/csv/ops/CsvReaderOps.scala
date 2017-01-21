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

import kantan.csv.{CsvReader, _}

// Alright, yes, this is nasty. There are abstractions designed to deal with just this situation, but not everyone
// knows about them / understands them / can afford to depend on libraries that provide them.

/** Provides useful syntax for `CsvReader[ReadResult[A]]`.
  *
  * When parsing CSV data, a very common scenario is to get an instance of [[CsvReader]] and then use common
  * combinators such as `map` and `flatMap` on it. This can be awkward when the actual interesting value is
  * itself within a [[ReadResult]] which also needs to be mapped into. [[CsvReaderOps]] provides shortcuts, such as:
  * {{{
  *   val reader: CsvReader[ReadResult[List[Int]]] = ???
  *
  *   // Not the most useful code in the world, but shows how one can map and filter directly on the nested value.
  *   reader.mapResult(_.sum).filterResult(_ % 2 == 0)
  * }}}
  */
final class CsvReaderOps[A](val as: CsvReader[ReadResult[A]]) extends AnyVal {
  /** Turns a `CsvReader[ReadResult[A]]` into a `CsvReader[ReadResult[B]]`. */
  def mapResult[B](f: A ⇒ B): CsvReader[ReadResult[B]] = as.map(_.map(f))

  /** Turns a `CsvReader[ReadResult[A]]` into a `CsvReader[ReadResult[B]]`. */
  def flatMapResult[B](f: A ⇒ ReadResult[B]): CsvReader[ReadResult[B]] = as.map(_.flatMap(f))

  /** Filters on all successful values that match the specified predicate. */
  def filterResult(f: A ⇒ Boolean): CsvReader[ReadResult[A]] = as.filter(_.exists(f))
}

trait ToCsvReaderOps {
  implicit def toCsvReaderOps[A](results: CsvReader[ReadResult[A]]): CsvReaderOps[A] = new CsvReaderOps(results)
}

object csvReader extends ToCsvReaderOps
