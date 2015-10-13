package tabulate.laws.discipline

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.{arbitrary => arb}
import tabulate._

object equality {
  def eq[A, B: Arbitrary](a1: B => A, a2: B => A)(f: (A, A) => Boolean): Boolean = {
          val samples = List.fill(100)(arb[B].sample).collect {
            case Some(a) => a
            case None => sys.error("Could not generate arbitrary values to compare two functions")
          }
          samples.forall(b => f(a1(b), a2(b)))
        }

  def cellDecoder[A](c1: CellDecoder[A], c2: CellDecoder[A])(f: (DecodeResult[A], DecodeResult[A]) => Boolean): Boolean =
    eq(c1.decode, c2.decode)(f)

  def cellEncoder[A: Arbitrary](c1: CellEncoder[A], c2: CellEncoder[A]): Boolean =
      eq(c1.encode, c2.encode)(_ == _)

  def rowDecoder[A](c1: RowDecoder[A], c2: RowDecoder[A])(f: (DecodeResult[A], DecodeResult[A]) => Boolean): Boolean =
      eq(c1.decode, c2.decode)(f)

  def rowEncoder[A: Arbitrary](c1: RowEncoder[A], c2: RowEncoder[A]): Boolean =
        eq(c1.encode, c2.encode)(_ == _)
}
