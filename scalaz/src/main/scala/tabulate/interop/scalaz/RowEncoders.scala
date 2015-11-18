package tabulate.interop.scalaz

import export.{export, exports}
import tabulate.{CellEncoder, RowEncoder}

import scalaz._

@exports
object RowEncoders {
  @export(Instantiated)
  implicit def eitherRowEncoder[A, B](implicit ea: RowEncoder[A], eb: RowEncoder[B]): RowEncoder[A \/ B] =
    RowEncoder(eab => eab match {
      case -\/(a)  => ea.encode(a)
      case \/-(b)  => eb.encode(b)
    })

  @export(Instantiated)
  implicit def foldableRowEncoder[A, F[_]](implicit ea: CellEncoder[A], F: Foldable[F]): RowEncoder[F[A]] =
    RowEncoder(as => F.foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += ea.encode(a)).result())
}
