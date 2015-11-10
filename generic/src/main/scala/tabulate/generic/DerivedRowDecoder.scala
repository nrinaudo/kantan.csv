package tabulate.generic

import shapeless._
import tabulate.{CellDecoder, DecodeResult, RowDecoder}

trait DerivedRowDecoder[A] extends RowDecoder[A]

@export.exports
object DerivedRowDecoder {
  def apply[A](f: Seq[String] => DecodeResult[A]): DerivedRowDecoder[A] = new DerivedRowDecoder[A] {
    override def decode(row: Seq[String]) = f(row)
  }



  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlist[H: CellDecoder, T <: HList: RowDecoder]: DerivedRowDecoder[H :: T] = DerivedRowDecoder(row =>
    row.headOption.map(s =>
      for {
        h <- CellDecoder[H].decode(s)
        t <- RowDecoder[T].decode(row.tail)
      } yield h :: t
    ).getOrElse(DecodeResult.decodeFailure))

  implicit val hnil: DerivedRowDecoder[HNil] = DerivedRowDecoder(_ => DecodeResult.success(HNil))

  implicit def caseObject[A, R <: HNil](implicit gen: Generic.Aux[A, R], ev: HNil =:= R): DerivedRowDecoder[A] =
    DerivedRowDecoder(s => if(s.isEmpty) DecodeResult.success(gen.from(ev(HNil))) else DecodeResult.decodeFailure)

  // The implicits here are a bit weird, but it's the only way I found to disambiguate between empty and non-empty
  // HLists. This is necessary to deal with case objects or case classes of arity 0.
  implicit def caseClass[A, H, R <: HList](implicit gen: Generic.Aux[A, R], ev: R <:< (H :: HList), d: RowDecoder[R]): DerivedRowDecoder[A] =
    DerivedRowDecoder(s => d.decode(s).map(gen.from))




  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproduct[H: RowDecoder, T <: Coproduct: RowDecoder]: DerivedRowDecoder[H :+: T] = DerivedRowDecoder(row =>
    RowDecoder[H].decode(row).map(Inl.apply).orElse(RowDecoder[T].decode(row).map(Inr.apply))
  )

  implicit val cnil: DerivedRowDecoder[CNil] = DerivedRowDecoder(_ => DecodeResult.decodeFailure)

  implicit def adt[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], d: RowDecoder[R]): DerivedRowDecoder[A] =
    DerivedRowDecoder(row => d.decode(row).map(gen.from))
}
