package tabulate.generic

import shapeless._
import simulacrum.op
import tabulate.CellEncoder
import tabulate.ops._

trait DerivedCellEncoder[A] extends CellEncoder[A]

@export.exports
object DerivedCellEncoder {
  def apply[A](f: A => String): DerivedCellEncoder[A] = new DerivedCellEncoder[A] {
    override def encode(a: A) = f(a)
  }


  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductCellEncoder[H: CellEncoder, T <: Coproduct: CellEncoder]: DerivedCellEncoder[H :+: T] =
    DerivedCellEncoder((a: H :+: T) => a match {
      case Inl(h) => h.asCsvCell
      case Inr(t) => t.asCsvCell
    })

  implicit val cnilCellEncoder: DerivedCellEncoder[CNil] =
    DerivedCellEncoder((_: CNil) => sys.error("trying to encode CNil, this should not happen"))

  implicit def adtCellEncoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], e: CellEncoder[R]): DerivedCellEncoder[A] =
    DerivedCellEncoder(a => e.encode(gen.to(a)))



  // - Case class cell encoding ----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // Thanks Travis Brown for that one:
  // http://stackoverflow.com/questions/33563111/deriving-type-class-instances-for-case-classes-with-exactly-one-field
  implicit def caseClassCellEncoder[A, R, H](implicit gen: Generic.Aux[A, R], ev: R <:< (H :: HNil), e: CellEncoder[H]): DerivedCellEncoder[A] =
    DerivedCellEncoder((a: A) => ev(gen.to(a)) match {
      case h :: t => e.encode(h)
    })
}
