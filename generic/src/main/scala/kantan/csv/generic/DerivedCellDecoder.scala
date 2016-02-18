package kantan.csv.generic

import kantan.csv.{CellDecoder, CsvResult}
import shapeless._

trait DerivedCellDecoder[A] extends CellDecoder[A]

@export.exports
object DerivedCellDecoder {
  def apply[A](f: String ⇒ CsvResult[A]): DerivedCellDecoder[A] = new DerivedCellDecoder[A] {
    override def decode(s: String) = f(s)
  }



  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproduct[H, T <: Coproduct](implicit dh: CellDecoder[H], dt: DerivedCellDecoder[T]): DerivedCellDecoder[H :+: T] =
    DerivedCellDecoder(row ⇒ dh.decode(row).map(Inl.apply).orElse(dt.decode(row).map(Inr.apply))
  )

  implicit val cnil: DerivedCellDecoder[CNil] = DerivedCellDecoder(_ ⇒ CsvResult.decodeError)

  implicit def adt[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], dr: DerivedCellDecoder[R]): DerivedCellDecoder[A] =
    DerivedCellDecoder(row ⇒ dr.decode(row).map(gen.from))




  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def caseObject[A, R <: HNil](implicit gen: Generic.Aux[A, R], ev: HNil =:= R): DerivedCellDecoder[A] =
    DerivedCellDecoder(s ⇒ if(s.isEmpty) CsvResult(gen.from(ev(HNil))) else CsvResult.decodeError)

  implicit def caseClass[A, R, H](implicit gen: Generic.Aux[A, R], ev: (H :: HNil) =:= R, dh: CellDecoder[H]): DerivedCellDecoder[A] =
    DerivedCellDecoder(s ⇒ dh.decode(s).map(h ⇒ gen.from(ev(h :: HNil))))
}
