package kantan.csv.laws.discipline

import kantan.csv
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.{arbitrary => arb}

object equality {
  def eq[A, B: Arbitrary](a1: B ⇒ A, a2: B ⇒ A)(f: (A, A) ⇒ Boolean): Boolean = {
    val samples = List.fill(100)(arb[B].sample).collect {
      case Some(a) ⇒ a
      case None ⇒ sys.error("Could not generate arbitrary values to compare two functions")
    }
    samples.forall(b ⇒ f(a1(b), a2(b)))
  }

  def cellDecoder[A](c1: csv.CellDecoder[A], c2: csv.CellDecoder[A])(f: (csv.DecodeResult[A], csv.DecodeResult[A]) ⇒ Boolean): Boolean =
    eq(c1.decode, c2.decode)(f)

  def cellEncoder[A: Arbitrary](c1: csv.CellEncoder[A], c2: csv.CellEncoder[A]): Boolean =
    eq(c1.encode, c2.encode)(_ == _)

  def rowDecoder[A](c1: csv.RowDecoder[A], c2: csv.RowDecoder[A])(f: (csv.DecodeResult[A], csv.DecodeResult[A]) ⇒ Boolean): Boolean =
    eq(c1.decode, c2.decode)(f)

  def rowEncoder[A: Arbitrary](c1: csv.RowEncoder[A], c2: csv.RowEncoder[A]): Boolean =
    eq(c1.encode, c2.encode)(_ == _)
}
