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
  implicit def hlistDecoder[H: CellDecoder, T <: HList: RowDecoder]: DerivedRowDecoder[H :: T] = DerivedRowDecoder(row =>
    row.headOption.map(s =>
      for {
        h <- CellDecoder[H].decode(s)
        t <- RowDecoder[T].decode(row.tail)
      } yield h :: t
    ).getOrElse(DecodeResult.decodeFailure))

  implicit val hnilDecoder: DerivedRowDecoder[HNil] = DerivedRowDecoder(_ => DecodeResult.success(HNil))

  implicit def caseClassDecoder[A, R <: HList](implicit gen: Generic.Aux[A, R], d: RowDecoder[R]): DerivedRowDecoder[A] =
    DerivedRowDecoder(s => d.decode(s).map(gen.from))



  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductRowDecoder[H: RowDecoder, T <: Coproduct: RowDecoder]: DerivedRowDecoder[H :+: T] = DerivedRowDecoder(row =>
    RowDecoder[H].decode(row).map(Inl.apply).orElse(RowDecoder[T].decode(row).map(Inr.apply))
  )

  implicit val cnilRowDecoder: DerivedRowDecoder[CNil] = DerivedRowDecoder(_ => DecodeResult.decodeFailure)

  implicit def adtRowDecoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], d: RowDecoder[R]): DerivedRowDecoder[A] =
    DerivedRowDecoder(row => d.decode(row).map(gen.from))
}
