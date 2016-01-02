package tabulate.laws

import java.util.UUID

import org.scalacheck.{Gen, Arbitrary}

/** Represents a value that cannot be decoded as an `A`. */
case class IllegalValue[A, B](value: B)

object IllegalValue {
  def arbitrary[A, B](gen: Gen[B]): Arbitrary[IllegalValue[A, B]] =
    Arbitrary(gen.map(IllegalValue.apply))

  private def illegalNum[A]: Arbitrary[IllegalCell[A]] = arbitrary(Gen.alphaChar.map(_.toString))

  implicit val arChar: Arbitrary[IllegalCell[Char]] = arbitrary(for {
    h <- Arbitrary.arbitrary[Char]
    t <- Gen.nonEmptyListOf(Arbitrary.arbitrary[Char])
  } yield (h :: t).mkString)
  implicit val arbInt: Arbitrary[IllegalCell[Int]] = illegalNum[Int]
  implicit val arbFloat: Arbitrary[IllegalCell[Float]] = illegalNum[Float]
  implicit val arbDouble: Arbitrary[IllegalCell[Double]] = illegalNum[Double]
  implicit val arbLong: Arbitrary[IllegalCell[Long]] = illegalNum[Long]
  implicit val arbByte: Arbitrary[IllegalCell[Byte]] = illegalNum[Byte]
  implicit val arbShort: Arbitrary[IllegalCell[Short]] = illegalNum[Short]
  implicit val arbBigInt: Arbitrary[IllegalCell[BigInt]] = illegalNum[BigInt]
  implicit val arbBigDecimal: Arbitrary[IllegalCell[BigDecimal]] = illegalNum[BigDecimal]
  implicit val arbUUID: Arbitrary[IllegalCell[UUID]] = illegalNum[UUID]
  implicit val arbBoolean: Arbitrary[IllegalCell[Boolean]] = arbitrary(Arbitrary.arbitrary[Int].map(_.toString))
  implicit def arbOption[A](implicit arb: Arbitrary[IllegalCell[A]]): Arbitrary[IllegalCell[Option[A]]] =
    arbitrary(arb.arbitrary.map(_.value))

  implicit def arbTraversable[A, C[X] <: Traversable[X]](implicit arb: Arbitrary[IllegalCell[A]]): Arbitrary[IllegalRow[C[A]]] =
    arbitrary(arb.arbitrary.map(s => Seq(s.value)))

  implicit def arbRow[A](implicit a: Arbitrary[IllegalCell[A]]): Arbitrary[IllegalRow[A]] =
    arbitrary(a.arbitrary.map(ia => Seq(ia.value)))
}