package tabulate.generic

import shapeless._
import tabulate.{DecodeResult, CellDecoder}
import tabulate.ops._

trait DerivedCellDecoder[A] extends CellDecoder[A]

@export.exports
object DerivedCellDecoder {
  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductCellDecoder[H: CellDecoder, T <: Coproduct: CellDecoder]: CellDecoder[H :+: T] =
    CellDecoder(row => CellDecoder[H].decode(row).map(Inl.apply).orElse(CellDecoder[T].decode(row).map(Inr.apply))
  )

  implicit val cnilCellDecoder: CellDecoder[CNil] = CellDecoder(_ => DecodeResult.decodeFailure)

  implicit def adtCellDecoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], d: CellDecoder[R]): CellDecoder[A] =
    CellDecoder(row => d.decode(row).map(gen.from))




  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def caseClassCellDecoder[A, R, H](implicit gen: Generic.Aux[A, R], ev: (H :: HNil) =:= R, d: CellDecoder[H]): CellDecoder[A] =
    CellDecoder(s => d.decode(s).map(h => gen.from(ev(h :: HNil))))
}
