package tabulate.laws

import java.util.UUID

import org.scalacheck.{Gen, Arbitrary}

/** Represents a value that cannot be decoded as an `A`. */
case class IllegalValue[A](value: String)

object IllegalValue {
  private def illegalNum[A]: Arbitrary[IllegalValue[A]] =
    Arbitrary(Gen.alphaChar.map(i => IllegalValue(i.toString)))

  implicit val arChar: Arbitrary[IllegalValue[Char]] = Arbitrary(for {
    h <- Arbitrary.arbitrary[Char]
    t <- Gen.nonEmptyListOf(Arbitrary.arbitrary[Char])
  } yield IllegalValue((h :: t).mkString))
  implicit val arbInt: Arbitrary[IllegalValue[Int]] = illegalNum[Int]
  implicit val arbFloat: Arbitrary[IllegalValue[Float]] = illegalNum[Float]
  implicit val arbDouble: Arbitrary[IllegalValue[Double]] = illegalNum[Double]
  implicit val arbLong: Arbitrary[IllegalValue[Long]] = illegalNum[Long]
  implicit val arbByte: Arbitrary[IllegalValue[Byte]] = illegalNum[Byte]
  implicit val arbShort: Arbitrary[IllegalValue[Short]] = illegalNum[Short]
  implicit val arbBigInt: Arbitrary[IllegalValue[BigInt]] = illegalNum[BigInt]
  implicit val arbBigDecimal: Arbitrary[IllegalValue[BigDecimal]] = illegalNum[BigDecimal]
  implicit val arbUUID: Arbitrary[IllegalValue[UUID]] = illegalNum[UUID]
  implicit val arbBoolean: Arbitrary[IllegalValue[Boolean]] = Arbitrary(Arbitrary.arbitrary[Int].map(i => IllegalValue(i.toString)))
  implicit def arbOption[A](implicit arb: Arbitrary[IllegalValue[A]]): Arbitrary[IllegalValue[Option[A]]] =
    Arbitrary(arb.arbitrary.map(a => IllegalValue(a.value)))
}