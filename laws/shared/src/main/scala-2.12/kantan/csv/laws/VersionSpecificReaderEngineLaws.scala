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
package laws

trait VersionSpecificReaderEngineLaws { self: ReaderEngineLaws =>

  def withFilter(csv: List[List[Cell]], f: List[Cell] => Boolean): Boolean =
    asReader(csv).withFilter(f).toList == asReader(csv).filter(f).toList

  def toStream(csv: List[List[Cell]]): Boolean =
    asReader(csv).toStream == csv.toStream

  def toTraversable(csv: List[List[Cell]]): Boolean =
    asReader(csv).toTraversable == csv.toTraversable

  def toIterator(csv: List[List[Cell]]): Boolean =
    asReader(csv).toIterator.sameElements(csv.toIterator)
}
