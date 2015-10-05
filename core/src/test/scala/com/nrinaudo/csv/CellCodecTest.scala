package com.nrinaudo.csv

import org.scalacheck.{Gen, Arbitrary}
import org.scalacheck.Gen._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import CellCodecTest._
import ops._

object CellCodecTest {
  // This is necessary to prevent ScalaCheck from generating BigDecimal values that cannot be serialized because their
  // scale is higher than MAX_INT.
  // Note that this isn't actually an issue with ScalaCheck but with Scala itself, and is(?) fixed in Scala 2.12:
  // https://github.com/scala/scala/pull/4320
  implicit lazy val arbBigDecimal: Arbitrary[BigDecimal] = {
    import java.math.MathContext._
    val mcGen = oneOf(DECIMAL32, DECIMAL64, DECIMAL128)
    val bdGen = for {
      x <- Arbitrary.arbitrary[BigInt]
      mc <- mcGen
      limit <- const(math.max(x.abs.toString.length - mc.getPrecision, 0))
      scale <- Gen.choose(Int.MinValue + limit , Int.MaxValue)
    } yield {
        try {
          BigDecimal(x, scale, mc)
        } catch {
          case ae: java.lang.ArithmeticException => BigDecimal(x, scale, UNLIMITED) // Handle the case where scale/precision conflict
        }
      }
    Arbitrary(bdGen)
  }
}

abstract class CellCodecTest[A: CellCodec: Arbitrary] extends FunSuite with GeneratorDrivenPropertyChecks {
  test("decode(encode(a)) must be equal to a for any a") {
    forAll { a: A =>
      assert(CellDecoder[A].decode(CellEncoder[A].encode(a)) == DecodeResult.Success(a))
    }
  }

  test("The covariant functor composition law must be respected") {
    forAll { (a: A, f: A => Int) =>
      assert(CellDecoder[A].decode(a.asCsvCell).map(f) == CellDecoder[A].map(f).decode(a.asCsvCell))
    }
  }

  test("The covariant functor identity law must be respected") {
    forAll { a: A =>
      assert(CellDecoder[A].decode(a.asCsvCell) == CellDecoder[A].map(identity).decode(a.asCsvCell))
    }
  }

  test("The contravariant functor composition law must be respected") {
    forAll { (i: Int, f: Int => A) =>
      assert(CellEncoder[A].encode(f(i)) == CellEncoder[A].contramap[Int](f).encode(i))
    }
  }

  test("The contravariant functor identity law must be respected") {
    forAll { a: A =>
      assert(CellEncoder[A].encode(a) == CellEncoder[A].contramap[A](identity).encode(a))
    }
  }
}

class StringCodecTest extends CellCodecTest[String]
class CharCodecTest extends CellCodecTest[Char]
class IntCodecTest extends CellCodecTest[Int]
class BigIntCodecTest extends CellCodecTest[BigInt]
class BigDecimalCodecTest extends CellCodecTest[BigDecimal]
class DoubleCodecTest extends CellCodecTest[Double]
class LongCodecTest extends CellCodecTest[Long]
class FloatCodecTest extends CellCodecTest[Float]
class ShortCodecTest extends CellCodecTest[Short]
class ByteCodecTest extends CellCodecTest[Byte]
class BooleanCodecTest extends CellCodecTest[Boolean]
class OptionCodecTest extends CellCodecTest[Option[Int]]
class EitherCodecTest extends CellCodecTest[Either[Int, Boolean]]