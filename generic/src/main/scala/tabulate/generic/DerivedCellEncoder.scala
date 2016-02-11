package tabulate.generic

import shapeless._
import tabulate.CellEncoder

trait DerivedCellEncoder[A] extends CellEncoder[A]

@export.exports
object DerivedCellEncoder {
  def apply[A](f: A ⇒ String): DerivedCellEncoder[A] = new DerivedCellEncoder[A] {
    override def encode(a: A) = f(a)
  }


  // - ADT derivation --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def coproduct[H, T <: Coproduct](implicit eh: CellEncoder[H], et: DerivedCellEncoder[T]): DerivedCellEncoder[H :+: T] =
    DerivedCellEncoder((a: H :+: T) ⇒ a match {
      case Inl(h) ⇒ eh.encode(h)
      case Inr(t) ⇒ et.encode(t)
    })

  implicit val cnil: DerivedCellEncoder[CNil] =
    DerivedCellEncoder((_: CNil) ⇒ sys.error("trying to encode CNil, this should not happen"))

  implicit def adt[A, R <: Coproduct](implicit gen: Generic.Aux[A, R], er: DerivedCellEncoder[R]): DerivedCellEncoder[A] =
    DerivedCellEncoder(a ⇒ er.encode(gen.to(a)))



  // - Case class cell encoding ----------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def caseObject[A, R <: HNil](implicit gen: Generic.Aux[A, R]): DerivedCellEncoder[A] =
    DerivedCellEncoder((_: A) ⇒ "")

  // Thanks Travis Brown for that one:
  // http://stackoverflow.com/questions/33563111/deriving-type-class-instances-for-case-classes-with-exactly-one-field
  implicit def caseClass[A, R, H](implicit gen: Generic.Aux[A, R], ev: R <:< (H :: HNil), eh: CellEncoder[H]): DerivedCellEncoder[A] =
    DerivedCellEncoder((a: A) ⇒ ev(gen.to(a)) match {
      case h :: t ⇒ eh.encode(h)
    })
}
