package tabulate.generic

import shapeless._
import tabulate.{CellDecoder, DecodeResult, RowDecoder}

trait DerivedRowDecoder[A] extends RowDecoder[A]

@export.exports
object DerivedRowDecoder {
  // - Case class derivation -------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistDecoder[H: CellDecoder, T <: HList: RowDecoder]: RowDecoder[H :: T] = RowDecoder(row =>
    row.headOption.map(s =>
      for {
        h <- CellDecoder[H].decode(s)
        t <- RowDecoder[T].decode(row.tail)
      } yield h :: t
    ).getOrElse(DecodeResult.decodeFailure))

  implicit val hnilDecoder: RowDecoder[HNil] = RowDecoder(_ => DecodeResult.success(HNil))

  implicit def caseClassDecoder[A, R <: HList](implicit gen: Generic.Aux[A, R], d: RowDecoder[R]): RowDecoder[A] =
    RowDecoder(s => d.decode(s).map(gen.from))


  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductRowDecoder[H: RowDecoder, T <: Coproduct: RowDecoder]: RowDecoder[H :+: T] = RowDecoder(row =>
    RowDecoder[H].decode(row).map(Inl.apply).orElse(RowDecoder[T].decode(row).map(Inr.apply))
  )

  implicit val cnilRowDecoder: RowDecoder[CNil] = RowDecoder(_ => DecodeResult.decodeFailure)

  implicit def adtRowDecoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], d: RowDecoder[R]): RowDecoder[A] =
    RowDecoder(row => d.decode(row).map(gen.from))
}
