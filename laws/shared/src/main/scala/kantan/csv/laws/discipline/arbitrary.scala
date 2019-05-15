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
package discipline

import imp.imp
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import org.scalacheck._, Arbitrary.{arbitrary => arb}
import org.scalacheck.rng.Seed

object arbitrary extends ArbitraryInstances

trait ArbitraryInstances extends kantan.codecs.laws.discipline.ArbitraryInstances {
  val csv: Gen[List[List[String]]] = arb[List[List[Cell]]].map(_.map(_.map(_.value)))

  implicit def arbTuple1[A: Arbitrary]: Arbitrary[Tuple1[A]] =
    Arbitrary(imp[Arbitrary[A]].arbitrary.map(Tuple1.apply))

  // - Errors ----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------

  val genOutOfBoundsError: Gen[DecodeError.OutOfBounds] = Gen.posNum[Int].map(DecodeError.OutOfBounds.apply)
  val genTypeError: Gen[DecodeError.TypeError]          = genException.map(DecodeError.TypeError.apply)
  val genDecodeError: Gen[DecodeError]                  = Gen.oneOf(genOutOfBoundsError, genTypeError)

  val genIOError: Gen[ParseError.IOError]                  = genIoException.map(ParseError.IOError.apply)
  val genNoSuchElement: Gen[ParseError.NoSuchElement.type] = Gen.const(ParseError.NoSuchElement)
  val genParseError: Gen[ParseError]                       = Gen.oneOf(genIOError, genNoSuchElement)

  val genReadError: Gen[ReadError] = Gen.oneOf(genParseError, genDecodeError)

  implicit val arbTypeError: Arbitrary[DecodeError.TypeError]             = Arbitrary(genTypeError)
  implicit val arbIOError: Arbitrary[ParseError.IOError]                  = Arbitrary(genIOError)
  implicit val arbNoSuchElement: Arbitrary[ParseError.NoSuchElement.type] = Arbitrary(genNoSuchElement)
  implicit val arbOutOfBounds: Arbitrary[DecodeError.OutOfBounds]         = Arbitrary(genOutOfBoundsError)
  implicit val arbDecodeError: Arbitrary[DecodeError]                     = Arbitrary(genDecodeError)
  implicit val arbParseError: Arbitrary[ParseError]                       = Arbitrary(genParseError)
  implicit val arbReadError: Arbitrary[ReadError]                         = Arbitrary(Gen.oneOf(genDecodeError, genParseError))

  implicit val cogenCsvIOError: Cogen[ParseError.IOError]                  = Cogen[String].contramap(_.message)
  implicit val cogenCsvNoSuchElement: Cogen[ParseError.NoSuchElement.type] = Cogen[Unit].contramap(_ => ())
  implicit val cogenCsvParseError: Cogen[ParseError] = Cogen { (seed: Seed, err: ParseError) =>
    err match {
      case error: ParseError.NoSuchElement.type => cogenCsvNoSuchElement.perturb(seed, error)
      case error: ParseError.IOError            => cogenCsvIOError.perturb(seed, error)
    }
  }

  implicit val cogenCsvOutOfBounds: Cogen[DecodeError.OutOfBounds] = Cogen[Int].contramap(_.index)
  implicit val cogenCsvTypeError: Cogen[DecodeError.TypeError]     = Cogen[String].contramap(_.message)
  implicit val cogenCsvDecodeError: Cogen[DecodeError] = Cogen { (seed: Seed, err: DecodeError) =>
    err match {
      case error: DecodeError.OutOfBounds => cogenCsvOutOfBounds.perturb(seed, error)
      case error: DecodeError.TypeError   => cogenCsvTypeError.perturb(seed, error)
    }
  }

  implicit val cogenCsvReadError: Cogen[ReadError] = Cogen { (seed: Seed, err: ReadError) =>
    err match {
      case error: DecodeError => cogenCsvDecodeError.perturb(seed, error)
      case error: ParseError  => cogenCsvParseError.perturb(seed, error)
    }
  }

  // - Codec values ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def arbLegalRow[A](implicit arb: Arbitrary[LegalCell[A]]): Arbitrary[LegalRow[A]] = Arbitrary {
    arb.arbitrary.map { c =>
      LegalValue(Seq(c.encoded), c.decoded)
    }
  }

  implicit def arbIllegalRow[A](implicit arb: Arbitrary[IllegalCell[A]]): Arbitrary[IllegalRow[A]] = Arbitrary {
    arb.arbitrary.map { c =>
      IllegalValue(Seq(c.encoded))
    }
  }

}
