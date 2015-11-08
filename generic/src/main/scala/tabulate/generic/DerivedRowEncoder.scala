package tabulate.generic

import shapeless._
import tabulate.ops._
import tabulate.{CellEncoder, RowEncoder}

trait DerivedRowEncoder[A] extends RowEncoder[A]

@export.exports
object DerivedRowEncoder {
  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistRowEncoder[H: CellEncoder, T <: HList: RowEncoder]: RowEncoder[H :: T] =
    RowEncoder((a: H :: T) => a match {
      case h :: t => h.asCsvCell +: t.asCsvRow
    })

  implicit val hnilRowEncoder: RowEncoder[HNil] = RowEncoder(_ => Seq.empty)

  implicit def caseClassRowEncoder[A, R <: HList](implicit gen: Generic.Aux[A, R], c: RowEncoder[R]): RowEncoder[A] =
    RowEncoder(a => c.encode(gen.to(a)))



  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductRowEncoder[H: RowEncoder, T <: Coproduct: RowEncoder]: RowEncoder[H :+: T] =
    RowEncoder((a: H :+: T) => a match {
      case Inl(h) => h.asCsvRow
      case Inr(t) => t.asCsvRow
    })

  implicit val cnilRowEncoder: RowEncoder[CNil] =
    RowEncoder((_: CNil) => sys.error("trying to encode CNil, this should not happen"))

  implicit def adtRowEncoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], e: RowEncoder[R]): RowEncoder[A] =
    RowEncoder(a => e.encode(gen.to(a)))
}