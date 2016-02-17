package kantan.csv

import simulacrum.{noop, typeclass}

import scala.collection.generic.CanBuildFrom

/** Decodes CSV rows into usable types.
  *
  * When implementing a custom [[RowDecoder]] instance, the correct way of parsing each cell is by retrieving a
  * [[CellDecoder]] for the correct type and delegating the job to it, combining the result through `map` and `flatMap`
  * (or for-comprehensions). For example:
  * {{{
  *   case class Point2D(x: Int, y: Int)
  *
  *   implicit val p2dDecoder = RowDecoder { ss ⇒
  *     for {
  *       x ← CellDecoder[Int].decode(ss, 0)
  *       y ← CellDecoder[Int].decode(ss, 1)
  *     } yield new Point2D(x, y)
  *   }
  * }}}
  *
  * See the [[RowDecoder$ companion object]] for default implementations and construction methods.
  */
@typeclass trait RowDecoder[A] { self ⇒
  /** Turns the content of a row into `A`. */
  @noop
  def decode(row: Seq[String]): DecodeResult[A]

  @noop
  def unsafeDecode(row: Seq[String]): A = decode(row).get

  /** Turns a `RowDecoder[A]` into a `RowDecoder[B]`.
    *
    * This allows developers to adapt existing instances of [[RowDecoder]] rather than write one from scratch.
    */
  @noop
  def map[B](f: A ⇒ B): RowDecoder[B] = RowDecoder(ss ⇒ decode(ss).map(f))
}

@export.imports[RowDecoder]
trait LowPriorityRowDecoders {
  implicit def cellDecoder[A](implicit da: CellDecoder[A]): RowDecoder[A] = RowDecoder(ss ⇒
    ss.headOption.map(h ⇒ if(ss.tail.isEmpty) da.decode(h) else DecodeResult.decodeFailure).getOrElse(DecodeResult.decodeFailure)
  )
}

/** Defines convenience methods for creating and retrieving instances of [[RowDecoder]].
  *
  * Implicit default implementations of standard types are also declared here, always bringing them in scope with a low
  * priority.
  *
  * Case classes have special creation methods: `decoderXXX`, where `XXX` is the number of fields in the case class.
  * You can just pass a case class' companion object's `apply` method, the list of field indexes, and get a
  * [[RowDecoder]].
  *
  * These default implementations can also be useful when writing more complex instances: if you need to write a
  * `RowDecoder[B]` and have both a `RowDecoder[A]` and a `A ⇒ B`, you need just use [[RowDecoder.map]] to create
  * your implementation.
  */
object RowDecoder extends LowPriorityRowDecoders with GeneratedRowDecoders {
  /** Creates a new instance of [[RowDecoder]] that uses the specified function to parse data. */
  def apply[A](f: Seq[String] ⇒ DecodeResult[A]): RowDecoder[A] = new RowDecoder[A] {
    override def decode(row: Seq[String]) = f(row)
  }

  def fromUnsafe[A](f: Seq[String] ⇒ A): RowDecoder[A] = new RowDecoder[A] {
    override def decode(row: Seq[String]) = DecodeResult(f(row))
    override def unsafeDecode(row: Seq[String]) = f(row)
  }

  /** Parses CSV rows as sequences of strings. */
  implicit val stringSeq: RowDecoder[Seq[String]] = RowDecoder(ss ⇒ DecodeResult.success(ss))


  /** Parses a CSV row into an `Either[A, B]`.
    *
    * This is done by first attempting to parse the row as an `A`. If that fails, we'll try parsing it as a `B`. If that
    * fails as well, [[DecodeResult.DecodeFailure]] will be returned.
    */
  implicit def either[A, B](implicit da: RowDecoder[A], db: RowDecoder[B]): RowDecoder[Either[A, B]] =
    RowDecoder { ss ⇒
      da.decode(ss).map(a ⇒ Left(a): Either[A, B]).orElse(db.decode(ss).map(b ⇒ Right(b): Either[A, B]))
    }

  implicit def option[A](implicit da: RowDecoder[A]): RowDecoder[Option[A]] = RowDecoder { ss ⇒
    if(ss.isEmpty) DecodeResult.success(None)
    else           da.decode(ss).map(a ⇒ Some(a))
  }

  /** Parses a CSV row into a collection of `A`. */
  implicit def collection[A, M[X]](implicit da: CellDecoder[A], cbf: CanBuildFrom[Nothing, A, M[A]]): RowDecoder[M[A]] =
    RowDecoder(ss ⇒ ss.foldLeft(DecodeResult.success(cbf.apply())) { (racc, s) ⇒ for {
      acc ← racc
      a   ← da.decode(s)
    } yield acc += a
    }.map(_.result()))
}
