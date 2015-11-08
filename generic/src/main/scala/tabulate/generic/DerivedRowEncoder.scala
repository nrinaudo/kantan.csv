package tabulate.generic

import shapeless._
import tabulate.ops._
import tabulate.{CellEncoder, RowEncoder}

trait DerivedRowEncoder[A] extends RowEncoder[A]

@export.exports
object DerivedRowEncoder {
  def apply[A](f: A => Seq[String]): DerivedRowEncoder[A] = new DerivedRowEncoder[A] {
    override def encode(a: A) = f(a)
  }



  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistRowEncoder[H: CellEncoder, T <: HList: RowEncoder]: DerivedRowEncoder[H :: T] =
    DerivedRowEncoder((a: H :: T) => a match {
      case h :: t => h.asCsvCell +: t.asCsvRow
    })

  implicit val hnilRowEncoder: DerivedRowEncoder[HNil] = DerivedRowEncoder(_ => Seq.empty)

  implicit def caseClassRowEncoder[A, R <: HList](implicit gen: Generic.Aux[A, R], c: RowEncoder[R]): DerivedRowEncoder[A] =
    DerivedRowEncoder(a => c.encode(gen.to(a)))



  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductRowEncoder[H: RowEncoder, T <: Coproduct: RowEncoder]: DerivedRowEncoder[H :+: T] =
    DerivedRowEncoder((a: H :+: T) => a match {
      case Inl(h) => h.asCsvRow
      case Inr(t) => t.asCsvRow
    })

  implicit val cnilRowEncoder: DerivedRowEncoder[CNil] =
    DerivedRowEncoder((_: CNil) => sys.error("trying to encode CNil, this should not happen"))

  implicit def adtRowEncoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], e: RowEncoder[R]): DerivedRowEncoder[A] =
    DerivedRowEncoder(a => e.encode(gen.to(a)))
}