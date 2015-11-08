package tabulate.interop.scalaz

import export.exports
import tabulate.{RowEncoder, CellEncoder}
import tabulate.ops._

import scalaz.{\/-, -\/, \/, Foldable}

trait DerivedRowEncoder[A] extends RowEncoder[A]

@exports
object DerivedRowEncoder {
  implicit def foldableRowEncoder[A: CellEncoder, F[_]: Foldable]: RowEncoder[F[A]] = new RowEncoder[F[A]] {
    override def encode(as: F[A]): Seq[String] = Foldable[F].foldLeft(as, Seq.newBuilder[String])((acc, a) => acc += a.asCsvCell).result()
  }

  /** [[RowEncoder]] instance for `\/`. */
  implicit def eitherRowEncoder[A: RowEncoder, B: RowEncoder]: RowEncoder[A \/ B] = RowEncoder(eab => eab match {
    case -\/(a)  => a.asCsvRow
    case \/-(b)  => b.asCsvRow
  })
}
