package tabulate

import org.scalacheck.{Gen, Arbitrary}
import shapeless._
import tabulate.laws.{IllegalCell, IllegalValue}

package object generic {
  type Illegal[A] = Arbitrary[IllegalCell[A]]

  implicit def caseObject[A, R <: HNil](implicit gen: Generic.Aux[A, R]): Illegal[A] =
    IllegalValue.arbitrary(Gen.const("!@#"))

  implicit def caseClass[A, R, H](implicit gen: Generic.Aux[A, R], ev: R <:< (H :: HNil), ih: Illegal[H]): Illegal[A] =
    IllegalValue.arbitrary(ih.arbitrary.map(_.value))
}
