package tabulate

import tabulate.ops._
import shapeless._

package object generic {
  // - ADT encoding ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductEncoder[H: RowEncoder, T <: Coproduct: RowEncoder]: RowEncoder[H :+: T] =
    RowEncoder((a: H :+: T) => a match {
      case Inl(h) => h.asCsvRow
      case Inr(t) => t.asCsvRow
    })

  implicit val cnilEncoder: RowEncoder[CNil] = RowEncoder((_: CNil) => sys.error("trying to encode CNil, this should not happen"))

  implicit def adtEncoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], e: RowEncoder[R]): RowEncoder[A] =
    RowEncoder(a => e.encode(gen.to(a)))



  // - ADT decoding ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproductDecoder[H: RowDecoder, T <: Coproduct: RowDecoder]: RowDecoder[H :+: T] = RowDecoder(row =>
    RowDecoder[H].decode(row).map(Inl.apply).orElse(RowDecoder[T].decode(row).map(Inr.apply))
  )

  implicit val cnilDecoder: RowDecoder[CNil] = RowDecoder(_ => DecodeResult.decodeFailure)

  implicit def adtDecoder[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], d: RowDecoder[R]): RowDecoder[A] =
    RowDecoder(row => d.decode(row).map(gen.from))


  // - Case class encoding ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def hlistEncoder[H: CellEncoder, T <: HList: RowEncoder]: RowEncoder[H :: T] = RowEncoder((a: H :: T) => a match {
    case h :: t => h.asCsvCell +: t.asCsvRow
  })

  implicit val hnilEncoder: RowEncoder[HNil] = RowEncoder(_ => Seq.empty)

  implicit def caseClassEncoder[A, R <: HList](implicit gen: Generic.Aux[A, R], c: RowEncoder[R]): RowEncoder[A] =
    RowEncoder(a => c.encode(gen.to(a)))



  // - Case class decoding ---------------------------------------------------------------------------------------------
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
    RowDecoder(a => d.decode(a).map(gen.from))
}
