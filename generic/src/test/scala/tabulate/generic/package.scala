package tabulate

import org.scalacheck.{Gen, Arbitrary}
import shapeless._
import tabulate.laws.IllegalValue

package object generic {
  type Illegal[A] = Arbitrary[IllegalValue[A]]

  implicit def caseObject[A, R <: HNil](implicit gen: Generic.Aux[A, R]): Illegal[A] =
    Arbitrary(Gen.const(IllegalValue("!@#")))

  implicit def caseClass[A, R, H](implicit gen: Generic.Aux[A, R], ev: R <:< (H :: HNil), ih: Illegal[H]): Illegal[A] =
    Arbitrary(ih.arbitrary.map(h => IllegalValue(h.value)))
}
