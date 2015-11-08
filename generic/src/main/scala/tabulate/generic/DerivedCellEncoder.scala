package tabulate.generic

import shapeless._
import tabulate.CellEncoder
import tabulate.ops._

trait DerivedCellEncoder[A] extends CellEncoder[A]

@export.exports
object DerivedCellEncoder {
  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductCellEncoder[H: CellEncoder, T <: Coproduct: CellEncoder]: CellEncoder[H :+: T] =
    CellEncoder((a: H :+: T) => a match {
      case Inl(h) => h.asCsvCell
      case Inr(t) => t.asCsvCell
    })

  implicit val cnilCellEncoder: CellEncoder[CNil] =
    CellEncoder((_: CNil) => sys.error("trying to encode CNil, this should not happen"))

  implicit def adtCellEncoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], e: CellEncoder[R]): CellEncoder[A] =
    CellEncoder(a => e.encode(gen.to(a)))



  // - Case class cell encoding ----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // Thanks Travis Brown for that one:
  // http://stackoverflow.com/questions/33563111/deriving-type-class-instances-for-case-classes-with-exactly-one-field
  implicit def caseClassCellEncoder[A, R, H](implicit gen: Generic.Aux[A, R], ev: R <:< (H :: HNil), e: CellEncoder[H]): CellEncoder[A] =
    CellEncoder((a: A) => ev(gen.to(a)) match {
      case h :: t => e.encode(h)
    })
}
