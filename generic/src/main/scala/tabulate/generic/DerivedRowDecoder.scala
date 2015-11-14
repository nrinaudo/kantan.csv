package tabulate.generic

import shapeless._
import tabulate.{CellDecoder, DecodeResult, RowDecoder}
import tabulate.ops._

trait DerivedRowDecoder[A] extends RowDecoder[A]

@export.exports
object DerivedRowDecoder {
  def apply[A](f: Seq[String] => DecodeResult[A]): DerivedRowDecoder[A] = new DerivedRowDecoder[A] {
    override def decode(row: Seq[String]) = f(row)
  }



  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlist[H: CellDecoder, T <: HList: DerivedRowDecoder]: DerivedRowDecoder[H :: T] = DerivedRowDecoder(row =>
    row.headOption.map(s =>
      for {
        h <- s.parseCsvCell[H]
        t <- row.tail.parseCsvRow[T]
      } yield h :: t
    ).getOrElse(DecodeResult.decodeFailure))

  implicit val hnil: DerivedRowDecoder[HNil] = DerivedRowDecoder(_ => DecodeResult.success(HNil))

  // Case objects or case classes of arity 1 are a special case: they only decode empty strings.
  implicit def caseObject[A, R <: HNil](implicit gen: Generic.Aux[A, R], ev: HNil =:= R): DerivedRowDecoder[A] =
    DerivedRowDecoder(s => if(s.isEmpty) DecodeResult.success(gen.from(ev(HNil))) else DecodeResult.decodeFailure)

  // Case classes of arity 1 are a special case: if the unique field has a row decoder, than we can consider that the
  // whole case class decodes exactly as its field does.
  implicit def caseClass1[A, R, H](implicit gen: Generic.Aux[A, R], ev: (H :: HNil) =:= R, dh: DerivedRowDecoder[H]): DerivedRowDecoder[A] =
    DerivedRowDecoder(s => dh.decode(s).map(h => gen.from(ev(h :: HNil))))

  // The implicits here are a bit weird, but it's the only way I found to disambiguate between empty and non-empty
  // HLists. This is necessary to deal with case objects or case classes of arity 0.
  implicit def caseClass[A, H, R <: HList](implicit gen: Generic.Aux[A, R], ev: R <:< (H :: HList), dr: DerivedRowDecoder[R]): DerivedRowDecoder[A] =
    DerivedRowDecoder(s => dr.decode(s).map(gen.from))



  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproduct[H: RowDecoder, T <: Coproduct: DerivedRowDecoder]: DerivedRowDecoder[H :+: T] = DerivedRowDecoder(row =>
    RowDecoder[H].decode(row).map(Inl.apply).orElse(RowDecoder[T].decode(row).map(Inr.apply))
  )

  implicit val cnil: DerivedRowDecoder[CNil] = DerivedRowDecoder(_ => DecodeResult.decodeFailure)

  implicit def adt[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], dr: RowDecoder[R]): DerivedRowDecoder[A] =
    DerivedRowDecoder(row => dr.decode(row).map(gen.from))
}
