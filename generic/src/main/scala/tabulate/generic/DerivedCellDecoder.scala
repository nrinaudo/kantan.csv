package tabulate.generic

import shapeless._
import simulacrum.noop
import tabulate.{DecodeResult, CellDecoder}
import tabulate.ops._

trait DerivedCellDecoder[A] extends CellDecoder[A]

@export.exports
object DerivedCellDecoder {
  def apply[A](f: String => DecodeResult[A]): DerivedCellDecoder[A] = new DerivedCellDecoder[A] {
    override def decode(s: String) = f(s)
  }



  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductCellDecoder[H: CellDecoder, T <: Coproduct: CellDecoder]: DerivedCellDecoder[H :+: T] =
    DerivedCellDecoder(row => CellDecoder[H].decode(row).map(Inl.apply).orElse(CellDecoder[T].decode(row).map(Inr.apply))
  )

  implicit val cnilCellDecoder: DerivedCellDecoder[CNil] = DerivedCellDecoder(_ => DecodeResult.decodeFailure)

  implicit def adtCellDecoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], d: CellDecoder[R]): DerivedCellDecoder[A] =
    DerivedCellDecoder(row => d.decode(row).map(gen.from))





  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def caseClassCellDecoder[A, R, H](implicit gen: Generic.Aux[A, R], ev: (H :: HNil) =:= R, d: CellDecoder[H]): DerivedCellDecoder[A] =
    DerivedCellDecoder(s => d.decode(s).map(h => gen.from(ev(h :: HNil))))
}
