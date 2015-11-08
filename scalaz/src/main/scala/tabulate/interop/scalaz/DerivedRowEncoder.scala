package tabulate.interop.scalaz

import export.exports
import tabulate.ops._
import tabulate.{CellEncoder, RowEncoder}

import scalaz._

trait DerivedRowEncoder[A] extends RowEncoder[A]

@exports
object DerivedRowEncoder {
  implicit def foldableRowEncoder[A: CellEncoder, F[_]: Foldable]: DerivedRowEncoder[F[A]] = new DerivedRowEncoder[F[A]] {
    override def encode(as: F[A]) = Foldable[F].foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += a.asCsvCell).result()
  }

  implicit def eitherRowEncoder[A: RowEncoder, B: RowEncoder]: DerivedRowEncoder[A \/ B] =
  new DerivedRowEncoder[Disjunction[A, B]] {
    override def encode(a: Disjunction[A, B]) = a match {
        case -\/(a)  => a.asCsvRow
        case \/-(b)  => b.asCsvRow
      }
  }
}
