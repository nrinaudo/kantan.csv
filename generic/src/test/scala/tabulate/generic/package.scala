package tabulate

import org.scalacheck.{Gen, Arbitrary}
import shapeless._
import tabulate.laws._
import tabulate.laws.discipline.arbitrary._

package object generic {
  implicit val arbIllegalHNilCell: Arbitrary[IllegalCell[HNil]] = illegal(Gen.const("!@#"))
  implicit val arbIllegalHNilRow: Arbitrary[IllegalRow[HNil]]   = illegal(Gen.const(Seq("!@#")))

  implicit def arbIllegalCaseObject[A, R <: HNil](implicit gen: Generic.Aux[A, R]): Arbitrary[IllegalCell[A]] =
    illegal(Gen.const("!@#"))

  implicit def arbIllegalHList[H, T <: HList](implicit ah: Arbitrary[IllegalCell[H]], at: Arbitrary[IllegalRow[T]]): Arbitrary[IllegalRow[H :: T]] =
    Arbitrary(ah.arbitrary.flatMap(h ⇒ at.arbitrary.map(t ⇒ IllegalValue(h.value +: t.value))))
  implicit def arbIllegalCaseClass[A, R <: HList](implicit gen: Generic.Aux[A, R], dr: Arbitrary[IllegalRow[R]]): Arbitrary[IllegalRow[A]] =
    illegal(dr.arbitrary.map(_.value))
}
