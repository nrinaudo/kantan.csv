package tabulate

import java.util.UUID

import simulacrum.{noop, op, typeclass}

/** Encodes values of type `A` into CSV cells.
  *
  * [[CellEncoder]] instances aren't meant to be used directly, but rather provide the backbone of the [[RowEncoder]]
  * mechanism.
  *
  * If you're working with data that contains dates and need to serialise these to valid ISO 8601 strings, for example,
  * you'll need to create a custom `CellEncoder[Date]`:
  * {{{
  *   implicit val dateEncoder = CellEncoder((d: Date) => new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(d))
  * }}}
  *
  * Once this is done, tabulate will be capable of serialising date fields without any further work. The following, for
  * example, will create a [[RowEncoder]] for triples of type `(Int, String, Date)`:
  * {{{
  *   RowEncoder[(Int, String, Date)]
  * }}}
  *
  * @see [[http://nrinaudo.github.io/tabulate/tut/serializing.html Tutorial]]
  */
@typeclass trait CellEncoder[A] {
  /** Turns the specified `A` into a CSV cell. */
  @op("asCsvCell") def encode(a: A): String

  /** Turns a `CellEncoder[A]` into a `CellEncoder[B]`.
    *
    * This allows developers to adapt existing instance of [[CellDecoder]] rather than write new ones.
    */
  @noop def contramap[B](f: B => A): CellEncoder[B] = CellEncoder(f andThen encode _)
}

/** Low priority implicit encoders. */
@export.imports[CellEncoder]
trait LowPriorityCellEncoders

object CellEncoder extends LowPriorityCellEncoders {
  /** Creates a new [[CellEncoder]] from the specified function. */
  def apply[A](f: A => String): CellEncoder[A] = new CellEncoder[A] {
    override def encode(a: A) = f(a)
  }

  /** Turns a `String` into a CSV cell. */
  implicit val string: CellEncoder[String]     = CellEncoder(s => s)
  /** Turns a `Char` into a CSV cell. */
  implicit val char  : CellEncoder[Char]       = CellEncoder(_.toString)
  /** Turns an `Int` into a CSV cell. */
  implicit val int   : CellEncoder[Int]        = CellEncoder(_.toString)
  /** Turns a `Float` into a CSV cell. */
  implicit val float : CellEncoder[Float]      = CellEncoder(_.toString)
  /** Turns a `Double` into a CSV cell. */
  implicit val double: CellEncoder[Double]     = CellEncoder(_.toString)
  /** Turns a `Long` into a CSV cell. */
  implicit val long  : CellEncoder[Long]       = CellEncoder(_.toString)
  /** Turns a `Short` into a CSV cell. */
  implicit val short : CellEncoder[Short]      = CellEncoder(_.toString)
  /** Turns a `Byte` into a CSV cell. */
  implicit val byte  : CellEncoder[Byte]       = CellEncoder(_.toString)
  /** Turns a `Boolean` into a CSV cell. */
  implicit val bool  : CellEncoder[Boolean]    = CellEncoder(_.toString)
  /** Turns a `BigInt` into a CSV cell. */
  implicit val bigInt: CellEncoder[BigInt]     = CellEncoder(_.toString)
  /** Turns a `BigDec` into a CSV cell. */
  implicit val bigDec: CellEncoder[BigDecimal] = CellEncoder(_.toString)
  /** Turns a `UUID` into a CSV cell. */
  implicit val uuid  : CellEncoder[UUID]       = CellEncoder(_.toString)

  /** Turns an `Option[A]` into a CSV cell, provided `A` has a [[CellEncoder]]. */
  implicit def opt[A](implicit ea: CellEncoder[A]): CellEncoder[Option[A]] =
    CellEncoder(oa => oa.map(ea.encode).getOrElse(""))

  /** Turns an `Either[A, B]` into a CSV cell, provided both `A` and `B` have a [[CellEncoder]]. */
  implicit def either[A, B](implicit ea: CellEncoder[A], eb: CellEncoder[B]): CellEncoder[Either[A, B]] =
    CellEncoder(eab => eab match {
      case Left(a)  => ea.encode(a)
      case Right(b) => eb.encode(b)
    })
}
