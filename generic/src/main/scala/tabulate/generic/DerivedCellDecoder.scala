package tabulate.generic

import shapeless._
import tabulate.{CellDecoder, DecodeResult}

trait DerivedCellDecoder[A] extends CellDecoder[A]

@export.exports
object DerivedCellDecoder {
  def apply[A](f: String => DecodeResult[A]): DerivedCellDecoder[A] = new DerivedCellDecoder[A] {
    override def decode(s: String) = f(s)
  }



  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproduct[H: CellDecoder, T <: Coproduct: DerivedCellDecoder]: DerivedCellDecoder[H :+: T] =
    DerivedCellDecoder(row => CellDecoder[H].decode(row).map(Inl.apply).orElse(CellDecoder[T].decode(row).map(Inr.apply))
  )

  implicit val cnil: DerivedCellDecoder[CNil] = DerivedCellDecoder(_ => DecodeResult.decodeFailure)

  implicit def adt[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], dr: DerivedCellDecoder[R]): DerivedCellDecoder[A] =
    DerivedCellDecoder(row => dr.decode(row).map(gen.from))




  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def caseObject[A, R <: HNil](implicit gen: Generic.Aux[A, R], ev: HNil =:= R): DerivedCellDecoder[A] =
    DerivedCellDecoder(s => if(s.isEmpty) DecodeResult.success(gen.from(ev(HNil))) else DecodeResult.decodeFailure)

  implicit def caseClass[A, R, H](implicit gen: Generic.Aux[A, R], ev: (H :: HNil) =:= R, dh: CellDecoder[H]): DerivedCellDecoder[A] =
    DerivedCellDecoder(s => dh.decode(s).map(h => gen.from(ev(h :: HNil))))
}
