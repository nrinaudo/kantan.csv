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

package kantan.csv.laws.discipline

import kantan.csv.engine.ReaderEngine
import kantan.csv.laws.ReaderEngineLaws
import org.scalacheck.Prop
import org.scalacheck.Prop.forAll

trait ReaderEngineTests
    extends RfcReaderTests with SpectrumReaderTests with KnownFormatsReaderTests with VersionSpecificReaderEngineTests {
  def laws: ReaderEngineLaws

  def readerEngine: RuleSet =
    new RuleSet {
      def name: String =
        "readerEngine"
      def bases: Seq[(String, RuleSet)] =
        Nil
      def parents: Seq[RuleSet] =
        Seq(rfc4180, csvSpectrum, knownFormats)
      def props: Seq[(String, Prop)] =
        Seq(
          "drop"                 -> forAll(laws.drop _),
          "dropWhile"            -> forAll(laws.dropWhile _),
          "take"                 -> forAll(laws.take _),
          "forall"               -> forAll(laws.forall _),
          "map"                  -> forAll(laws.map _),
          "flatMap"              -> forAll(laws.flatMap _),
          "find"                 -> forAll(laws.find _),
          "exists"               -> forAll(laws.exists _),
          "filter"               -> forAll(laws.filter _),
          "next on empty"        -> forAll(laws.nextOnEmpty _),
          "next on empty (take)" -> forAll(laws.nextOnEmptyTake _),
          "hasDefiniteSize"      -> forAll(laws.hasDefiniteSize _),
          "isEmpty"              -> forAll(laws.isEmpty _),
          "isTraversableAgain"   -> forAll(laws.isTraversableAgain _)
        )
    }
}

object ReaderEngineTests {
  def apply(engine: ReaderEngine): ReaderEngineTests =
    new ReaderEngineTests {
      override def laws: ReaderEngineLaws =
        ReaderEngineLaws(engine)
    }
}
