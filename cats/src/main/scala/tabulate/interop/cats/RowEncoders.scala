package tabulate.interop.cats

import cats.Foldable
import cats.data.Xor
import export.{export, exports}
import tabulate.{CellEncoder, RowEncoder}

@exports
object RowEncoders {
  @export(Instantiated)
  implicit def xorRowEncoder[A, B](implicit ea: RowEncoder[A], eb: RowEncoder[B]): RowEncoder[Xor[A, B]] =
    RowEncoder(xab => xab match {
      case Xor.Left(a)  => ea.encode(a)
      case Xor.Right(b) => eb.encode(b)
    })

  @export(Instantiated)
  implicit def foldableRowEncoder[A, F[_]](implicit ea: CellEncoder[A], F: Foldable[F]): RowEncoder[F[A]] =
    RowEncoder(as => F.foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += ea.encode(a)).result())
}
