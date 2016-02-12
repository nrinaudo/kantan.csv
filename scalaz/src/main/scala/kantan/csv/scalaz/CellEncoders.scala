package kantan.csv.scalaz

import export.{export, exports}
import kantan.csv.CellEncoder

import scalaz.{-\/, Maybe, \/, \/-}

@exports
object CellEncoders {
  @export(Orphan)
  implicit def eitherCellEncoder[A, B](implicit ea: CellEncoder[A], eb: CellEncoder[B]): CellEncoder[A \/ B] =
  new CellEncoder[\/[A, B]] {
    override def encode(eab: \/[A, B]) = eab match {
        case -\/(a)  ⇒ ea.encode(a)
        case \/-(b)  ⇒ eb.encode(b)
      }
  }

  @export(Orphan)
  implicit def maybeEncoder[A](implicit ea: CellEncoder[A]): CellEncoder[Maybe[A]] = new CellEncoder[Maybe[A]] {
    override def encode(a: Maybe[A]) = a.map(ea.encode).getOrElse("")
  }
}
