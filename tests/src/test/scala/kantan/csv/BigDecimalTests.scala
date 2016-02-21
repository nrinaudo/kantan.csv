package kantan.csv

import kantan.csv.laws.discipline.CellCodecTests
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class BigDecimalTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // This is necessary to prevent ScalaCheck from generating BigDecimal values that cannot be serialized because their
  // scale is higher than MAX_INT.
  // Note that this isn't actually an issue with ScalaCheck but with Scala itself, and is(?) fixed in Scala 2.12:
  // https://github.com/scala/scala/pull/4320
  implicit lazy val arbBigDecimal: Arbitrary[BigDecimal] = {
    import java.math.MathContext._
    val mcGen = oneOf(DECIMAL32, DECIMAL64, DECIMAL128)
    val bdGen = for {
      x ← Arbitrary.arbitrary[BigInt]
      mc ← mcGen
      limit ← const(math.max(x.abs.toString.length - mc.getPrecision, 0))
      scale ← Gen.choose(Int.MinValue + limit , Int.MaxValue)
    } yield {
        try {
          BigDecimal(x, scale, mc)
        } catch {
          case ae: java.lang.ArithmeticException ⇒ BigDecimal(x, scale, UNLIMITED) // Handle the case where scale/precision conflict
        }
      }
    Arbitrary(bdGen)
  }

  checkAll("BigDecimal", CellCodecTests[BigDecimal].codec[String, Float])
}