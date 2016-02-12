package kantan.csv.generic

import kantan.csv
import kantan.csv.{CellDecoder, DecodeResult}
import shapeless._

trait DerivedRowDecoder[A] extends csv.RowDecoder[A]

@export.exports
object DerivedRowDecoder {
  def apply[A](f: Seq[String] ⇒ DecodeResult[A]): DerivedRowDecoder[A] = new DerivedRowDecoder[A] {
    override def decode(row: Seq[String]) = f(row)
  }



  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlist[H, T <: HList](implicit dh: CellDecoder[H], dt: DerivedRowDecoder[T]): DerivedRowDecoder[H :: T] =
    DerivedRowDecoder(row ⇒
      row.headOption.map(s ⇒
        for {
          h ← dh.decode(s)
          t ← dt.decode(row.tail)
        } yield h :: t
      ).getOrElse(DecodeResult.decodeFailure))

  implicit val hnil: DerivedRowDecoder[HNil] = DerivedRowDecoder(_ ⇒ DecodeResult.success(HNil))

  // Case objects or case classes of arity 0 are a special case: they only decode empty strings.
  implicit def caseObject[A, R <: HNil](implicit gen: Generic.Aux[A, R], ev: HNil =:= R): DerivedRowDecoder[A] =
    DerivedRowDecoder(s ⇒ if(s.isEmpty) DecodeResult.success(gen.from(ev(HNil))) else DecodeResult.decodeFailure)

  // Case classes of arity 1 are a special case: if the unique field has a row decoder, than we can consider that the
  // whole case class decodes exactly as its field does.
  implicit def caseClass1[A, H, R <: HList](implicit gen: Generic.Aux[A, R], ev: (H :: HNil) =:= R, dh: DerivedRowDecoder[H]): DerivedRowDecoder[A] =
    DerivedRowDecoder(s ⇒ dh.decode(s).map(h ⇒ gen.from(ev(h :: HNil))))

  // Case class of arity 2+
  implicit def caseClassN[A, H1, H2, R <: HList](implicit gen: Generic.Aux[A, R], ev: R <:< (H1 :: H2 :: HList), dr: DerivedRowDecoder[R]): DerivedRowDecoder[A] =
    DerivedRowDecoder(s ⇒ dr.decode(s).map(gen.from))



  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproduct[H, T <: Coproduct](implicit dh: csv.RowDecoder[H], dt: DerivedRowDecoder[T]): DerivedRowDecoder[H :+: T] =
    DerivedRowDecoder(row ⇒ dh.decode(row).map(Inl.apply).orElse(dt.decode(row).map(Inr.apply)))

  implicit val cnil: DerivedRowDecoder[CNil] = DerivedRowDecoder(_ ⇒ DecodeResult.decodeFailure)

  implicit def adt[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], dr: csv.RowDecoder[R]): DerivedRowDecoder[A] =
    DerivedRowDecoder(row ⇒ dr.decode(row).map(gen.from))
}
