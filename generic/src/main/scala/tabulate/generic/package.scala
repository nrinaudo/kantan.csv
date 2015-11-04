package tabulate

import tabulate.ops._
import shapeless._

package object generic {
  implicit def hlistEncoder[H: CellEncoder, T <: HList: RowEncoder]: RowEncoder[H :: T] = RowEncoder((a: H :: T) => a match {
    case h :: t => h.asCsvCell +: t.asCsvRow
  })

  implicit val hnilEncoder: RowEncoder[HNil] = RowEncoder(_ => Seq.empty)

  implicit def caseClassEncoder[A, R <: HList](implicit gen: Generic.Aux[A, R], c: RowEncoder[R]): RowEncoder[A] =
    RowEncoder(a => c.encode(gen.to(a)))

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
