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

package kantan.csv.engine.opencsv

import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class OpenCsvReaderTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline
  // TODO: opencsv fails a *lot* of the tests. So much that something might be wrong with my implementation of the
  // connector rather than with opencsv itself.
  //checkAll("OpenCsvReader", ReaderEngineTests(reader).readerEngine)
