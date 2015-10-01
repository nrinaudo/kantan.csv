package com.nrinaudo.csv

import org.scalacheck.{Gen, Arbitrary}
import org.scalacheck.Gen._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import CellFormatTest._
import ops._

object CellFormatTest {
  // This is necessary to prevent ScalaCheck from generating BigDecimal values that cannot be serialized because their
  // scale is higher than MAX_INT.
  // Note that this isn't actually an issue with ScalaCheck but with Scala itself, and is(?) fixed in Scala 2.12:
  // https://github.com/scala/scala/pull/4320
  implicit lazy val arbBigDecimal: Arbitrary[BigDecimal] = {
    import java.math.MathContext._
    val mcGen = oneOf(UNLIMITED, DECIMAL32, DECIMAL64, DECIMAL128)
    val bdGen = for {
      x <- Arbitrary.arbitrary[BigInt]
      mc <- mcGen
      limit <- const(if(mc == UNLIMITED) 0 else math.max(x.abs.toString.length - mc.getPrecision, 0))
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

abstract class CellFormatTest[A: CellFormat: Arbitrary] extends FunSuite with GeneratorDrivenPropertyChecks {
  test("read(write(a)) must be equal to a for any a") {
    forAll { a: A =>
      assert(CellReader[A].read(CellWriter[A].write(a)) == Some(a))
    }
  }

  test("The covariant functor composition law must be respected") {
    forAll { (a: A, f: A => Int) =>
      assert(CellReader[A].read(a.asCsvCell).map(f) == CellReader[A].map(f).read(a.asCsvCell))
    }
  }

  test("The covariant functor identity law must be respected") {
    forAll { a: A =>
      assert(CellReader[A].read(a.asCsvCell) == CellReader[A].map(identity).read(a.asCsvCell))
    }
  }

  test("The contravariant functor composition law must be respected") {
    forAll { (i: Int, f: Int => A) =>
      assert(CellWriter[A].write(f(i)) == CellWriter[A].contramap[Int](f).write(i))
    }
  }

    test("The contravariant functor identity law must be respected") {
      forAll { a: A =>
        assert(CellWriter[A].write(a) == CellWriter[A].contramap[A](identity).write(a))
      }
    }
}

class StringFormatTest extends CellFormatTest[String]
class CharFormatTest extends CellFormatTest[Char]
class IntFormatTest extends CellFormatTest[Int]
class BigIntFormatTest extends CellFormatTest[BigInt]
class BigDecimalFormatTest extends CellFormatTest[BigDecimal]
class DoubleFormatTest extends CellFormatTest[Double]
class LongFormatTest extends CellFormatTest[Long]
class FloatFormatTest extends CellFormatTest[Float]
class ShortFormatTest extends CellFormatTest[Short]
class ByteFormatTest extends CellFormatTest[Byte]
class BooleanFormatTest extends CellFormatTest[Boolean]
class OptionFormatTest extends CellFormatTest[Option[Int]]
class EitherFormatTest extends CellFormatTest[Either[Int, Boolean]]