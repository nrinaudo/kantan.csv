package tabulate

import simulacrum.{noop, typeclass}
import ops._

import scala.collection.generic.CanBuildFrom

/** Decodes CSV rows into usable types.
  *
  * When implementing a custom `RowDecoder` instance, the correct way of parsing each cell is by retrieving a
  * `CellDecoder` for the correct type and delegating the job to it, combining the result through `map` and `flatMap`
  * (or for-comprehensions). For example:
  * {{{
  *   case class Point2D(x: Int, y: Int)
  *
  *   implicit val p2dDecoder = RowDecoder { ss =>
  *     for {
  *       x <- CellDecoder[Int].decode(ss, 0)
  *       y <- CellDecoder[Int].decode(ss, 1)
  *     } yield new Point2D(x, y)
  *   }
  * }}}
  *
  * See the [[RowDecoder$ companion object]] for default implementations and construction methods.
  */
@typeclass trait RowDecoder[A] { self =>
  /** Turns the content of a row into `A`. */
  @noop def decode(row: Seq[String]): DecodeResult[A]

  /** Turns a `RowDecoder[A]` into a `RowDecoder[B]`.
    *
    * This allows developers to adapt existing instances of `RowDecoder` rather than write one from scratch.
    */
  @noop def map[B](f: A => B): RowDecoder[B] = RowDecoder(ss => decode(ss).map(f))

  @noop def flatMap[B](f: A => RowDecoder[B]): RowDecoder[B] = RowDecoder(s => decode(s).flatMap(a => f(a).decode(s)))
}

@export.imports[RowDecoder]
trait LowPriorityRowDecoders {
  /** Parses a CSV row into a collection of `A`. */
  implicit def collection[A, M[X]](implicit da: CellDecoder[A], cbf: CanBuildFrom[Nothing, A, M[A]]): RowDecoder[M[A]] =
    RowDecoder(ss => ss.foldLeft(DecodeResult.success(cbf.apply())) { (racc, s) => for {
      acc <- racc
      a   <- da.decode(s)
    } yield acc += a
    }.map(_.result()))

  implicit def cellDecoder[A](da: CellDecoder[A]): RowDecoder[A] = RowDecoder(ss =>
    ss.headOption.map(h => if(ss.tail.isEmpty) da.decode(h) else DecodeResult.decodeFailure).getOrElse(DecodeResult.decodeFailure)
  )
}

/** Defines convenience methods for creating and retrieving instances of `RowDecoder`.
  *
  * Implicit default implementations of standard types are also declared here, always bringing them in scope with a low
  * priority.
  *
  * Case classes have special creation methods: `decoderXXX`, where `XXX` is the number of fields in the case class.
  * You can just pass a case class' companion object's `apply` method, the list of field indexes, and get a
  * `RowDecoder`.
  *
  * These default implementations can also be useful when writing more complex instances: if you need to write a
  * `RowDecoder[B]` and have both a `RowDecoder[A]` and a `A => B`, you need just use [[RowDecoder.map]] to create
  * your implementation.
  */
object RowDecoder extends LowPriorityRowDecoders {
  /** Creates a new instance of [[RowDecoder]] that uses the specified function to parse data. */
  def apply[A](f: Seq[String] => DecodeResult[A]): RowDecoder[A] = new RowDecoder[A] {
    override def decode(row: Seq[String]) = f(row)
  }

  /** Parses CSV rows as sequences of strings. */
  implicit val stringSeq: RowDecoder[Seq[String]] = RowDecoder(ss => DecodeResult.success(ss))


  /** Parses a CSV row into an `Either[A, B]`.
    *
    * This is done by first attempting to parse the row as an `A`. If that fails, we'll try parsing it as a `B`. If that
    * fails as well, [[DecodeResult.DecodeFailure]] will be returned.
    */
  implicit def either[A, B](implicit da: RowDecoder[A], db: RowDecoder[B]): RowDecoder[Either[A, B]] =
    RowDecoder { ss =>
      da.decode(ss).map(a => Left(a): Either[A, B]).orElse(db.decode(ss).map(b => Right(b): Either[A, B]))
    }

  implicit def option[A](da: RowDecoder[A]): RowDecoder[Option[A]] = RowDecoder { ss =>
    if(ss.isEmpty) DecodeResult.success(None)
    else           da.decode(ss).map(a => Some(a))
  }



  // - Case class decoders ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // I am not proud of this, but I don't know of any other way to deal with non "curryable" types.

  /** Helper function to reduce the amount of boilerplate required by dealing with case classes. */
  @inline private def r[A](ss: Seq[String], index: Int)(implicit da: CellDecoder[A]): DecodeResult[A] =
    da.decode(ss, index)

  /** Creates a `RowDecoder` for a case class with 1 field. */
  def decoder1[A0: CellDecoder, R](f: A0 => R): RowDecoder[R] = RowDecoder(ss => r[A0](ss, 0).map(f))

  /** Creates a `RowDecoder` for a case class with 2 fields. */
  def decoder2[A0: CellDecoder, A1: CellDecoder, R](f: (A0, A1) => R)(i0: Int, i1: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1)) yield f(f0, f1))

  /** Creates a `RowDecoder` for a case class with 3 fields. */
  def decoder3[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, R]
  (f: (A0, A1, A2) => R)(i0: Int, i1: Int, i2: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2)) yield f(f0, f1, f2))

  /** Creates a `RowDecoder` for a case class with 4 fields. */
  def decoder4[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, R]
  (f: (A0, A1, A2, A3) => R)(i0: Int, i1: Int, i2: Int, i3: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3)) yield
      f(f0, f1, f2, f3))

  /** Creates a `RowDecoder` for a case class with 5 fields. */
  def decoder5[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4) => R)(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4)) yield f(f0, f1, f2, f3, f4))

  /** Creates a `RowDecoder` for a case class with 6 fields. */
  def decoder6[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5) => R)(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5)) yield f(f0, f1, f2, f3, f4, f5))

  /** Creates a `RowDecoder` for a case class with 7 fields. */
  def decoder7[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, R](f: (A0, A1, A2, A3, A4, A5, A6) => R)
                     (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6)) yield
      f(f0, f1, f2, f3, f4, f5, f6))

  /** Creates a `RowDecoder` for a case class with 8 fields. */
  def decoder8[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7)) yield
      f(f0, f1, f2, f3, f4, f5, f6, f7))

  /** Creates a `RowDecoder` for a case class with 9 fields. */
  def decoder9[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8)) yield f(f0, f1, f2, f3, f4, f5, f6, f7, f8))

  /** Creates a `RowDecoder` for a case class with 10 fields. */
  def decoder10[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9))
      yield f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9))

  /** Creates a `RowDecoder` for a case class with 11 fields. */
  def decoder11[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10))
      yield f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10))

  /** Creates a `RowDecoder` for a case class with 12 fields. */
  def decoder12[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int,
   i10: Int, i11: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11))
      yield f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11))

  /** Creates a `RowDecoder` for a case class with 13 fields. */
  def decoder13[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int,
   i10: Int, i11: Int, i12: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                         f12 <- r[A12](ss, i12)) yield
      f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12))

  /** Creates a `RowDecoder` for a case class with 14 fields. */
  def decoder14[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder,
  A13: CellDecoder, R](f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13) => R)
                      (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int,
                       i10: Int, i11: Int, i12: Int, i13: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                         f12 <- r[A12](ss, i12); f13 <- r[A13](ss, i13)) yield
      f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13))

  /** Creates a `RowDecoder` for a case class with 15 fields. */
  def decoder15[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder,
  A13: CellDecoder, A14: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                         f12 <- r[A12](ss, i12); f13 <- r[A13](ss, i13); f14 <- r[A14](ss, i14)) yield
      f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14))

  /** Creates a `RowDecoder` for a case class with 16 fields. */
  def decoder16[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder,
  A13: CellDecoder, A14: CellDecoder, A15: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                         f12 <- r[A12](ss, i12); f13 <- r[A13](ss, i13); f14 <- r[A14](ss, i14); f15 <- r[A15](ss, i15)
    ) yield f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15))

  /** Creates a `RowDecoder` for a case class with 17 fields. */
  def decoder17[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder,
  A13: CellDecoder, A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                         f12 <- r[A12](ss, i12); f13 <- r[A13](ss, i13); f14 <- r[A14](ss, i14); f15 <- r[A15](ss, i15);
                         f16 <- r[A16](ss, i16)) yield
      f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16))

  /** Creates a `RowDecoder` for a case class with 18 fields. */
  def decoder18[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder,
  A13: CellDecoder, A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                         f12 <- r[A12](ss, i12); f13 <- r[A13](ss, i13); f14 <- r[A14](ss, i14); f15 <- r[A15](ss, i15);
                         f16 <- r[A16](ss, i16); f17 <- r[A17](ss, i17)) yield
      f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17))

  /** Creates a `RowDecoder` for a case class with 19 fields. */
  def decoder19[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder,
  A13: CellDecoder, A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                         f12 <- r[A12](ss, i12); f13 <- r[A13](ss, i13); f14 <- r[A14](ss, i14); f15 <- r[A15](ss, i15);
                         f16 <- r[A16](ss, i16); f17 <- r[A17](ss, i17); f18 <- r[A18](ss, i18)) yield
      f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18))

  /** Creates a `RowDecoder` for a case class with 20 fields. */
  def decoder20[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder,
  A13: CellDecoder, A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder,
  A19: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                         f12 <- r[A12](ss, i12); f13 <- r[A13](ss, i13); f14 <- r[A14](ss, i14); f15 <- r[A15](ss, i15);
                         f16 <- r[A16](ss, i16); f17 <- r[A17](ss, i17); f18 <- r[A18](ss, i18); f19 <- r[A19](ss, i19)
    ) yield f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19))

  /** Creates a `RowDecoder` for a case class with 21 fields. */
  def decoder21[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder,
  A13: CellDecoder, A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder,
  A19: CellDecoder, A20: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                         f12 <- r[A12](ss, i12); f13 <- r[A13](ss, i13); f14 <- r[A14](ss, i14); f15 <- r[A15](ss, i15);
                         f16 <- r[A16](ss, i16); f17 <- r[A17](ss, i17); f18 <- r[A18](ss, i18); f19 <- r[A19](ss, i19);
                         f20 <- r[A20](ss, i20)) yield
      f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20))

  /** Creates a `RowDecoder` for a case class with 22 fields. */
  def decoder22[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder,
  A13: CellDecoder, A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder,
  A19: CellDecoder, A20: CellDecoder, A21: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int, i21: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                         f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                         f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                         f12 <- r[A12](ss, i12); f13 <- r[A13](ss, i13); f14 <- r[A14](ss, i14); f15 <- r[A15](ss, i15);
                         f16 <- r[A16](ss, i16); f17 <- r[A17](ss, i17); f18 <- r[A18](ss, i18); f19 <- r[A19](ss, i19);
                         f20 <- r[A20](ss, i20); f21 <- r[A21](ss, i21)) yield
      f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20, f21))


  // - Tuple decoders --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** Turns a row into a 1-uple. */
  implicit def tuple1[A1: CellDecoder]: RowDecoder[Tuple1[A1]] =
    decoder1(Tuple1.apply[A1])

  /** Turns a row into a 2-uple. */
  implicit def tuple2[A1: CellDecoder, A2: CellDecoder]: RowDecoder[(A1, A2)] =
    decoder2(Tuple2.apply[A1, A2])(0, 1)

  /** Turns a row into a 3-uple. */
  implicit def tuple3[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder]: RowDecoder[(A1, A2, A3)] =
    decoder3(Tuple3.apply[A1, A2, A3])(0, 1, 2)

  /** Turns a row into a 4-uple. */
  implicit def tuple4[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder]: RowDecoder[(A1, A2, A3, A4)] =
    decoder4(Tuple4.apply[A1, A2, A3, A4])(0, 1, 2, 3)

  /** Turns a row into a 5-uple. */
  implicit def tuple5[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5)] = decoder5(Tuple5.apply[A1, A2, A3, A4, A5])(0, 1, 2, 3, 4)

  /** Turns a row into a 6-uple. */
  implicit def tuple6[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6)] = decoder6(Tuple6.apply[A1, A2, A3, A4, A5, A6])(0, 1, 2, 3, 4, 5)

  /** Turns a row into a 7-uple. */
  implicit def tuple7[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7)] =
    decoder7(Tuple7.apply[A1, A2, A3, A4, A5, A6, A7])(0, 1, 2, 3, 4, 5, 6)

  /** Turns a row into a 8-uple. */
  implicit def tuple8[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8)] =
    decoder8(Tuple8.apply[A1, A2, A3, A4, A5, A6, A7, A8])(0, 1, 2, 3, 4, 5, 6, 7)

  /** Turns a row into a 9-uple. */
  implicit def tuple9[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9)] =
    decoder9(Tuple9.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9])(0, 1, 2, 3, 4, 5, 6, 7, 8)

  /** Turns a row into a 10-uple. */
  implicit def tuple10[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)] =
    decoder10(Tuple10.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

  /** Turns a row into a 11-uple. */
  implicit def tuple11[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)] =
    decoder11(Tuple11.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

  /** Turns a row into a 12-uple. */
  implicit def tuple12[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)] =
    decoder12(Tuple12.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

  /** Turns a row into a 13-uple. */
  implicit def tuple13[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)] =
    decoder13(Tuple13.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
      10, 11, 12)

  /** Turns a row into a 14-uple. */
  implicit def tuple14[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)] =
    decoder14(Tuple14.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14])(0, 1, 2, 3, 4, 5, 6, 7, 8,
      9, 10, 11, 12, 13)

  /** Turns a row into a 15-uple. */
  implicit def tuple15[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)] =
    decoder15(Tuple15.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12, 13, 14)

  /** Turns a row into a 16-uple. */
  implicit def tuple16[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)] =
    decoder16(Tuple16.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16])(0, 1, 2, 3, 4,
      5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

  /** Turns a row into a 17-uple. */
  implicit def tuple17[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)] =
    decoder17(Tuple17.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17])(0, 1, 2, 3,
      4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)

  /** Turns a row into a 18-uple. */
  implicit def tuple18[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)] =
    decoder18(Tuple18.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18])(0, 1,
      2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)

  /** Turns a row into a 19-uple. */
  implicit def tuple19[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder, A19: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19)] =
    decoder19(Tuple19.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18,
      A19])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)

  /** Turns a row into a 20-uple. */
  implicit def tuple20[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder, A19: CellDecoder, A20: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20)] =
    decoder20(Tuple20.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19,
      A20])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)

  /** Turns a row into a 21-uple. */
  implicit def tuple21[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder, A19: CellDecoder,
  A20: CellDecoder, A21: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16,
    A17, A18, A19, A20, A21)] = decoder21(Tuple21.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
    15, 16, 17, 18, 19, 20)

  /** Turns a row into a 22-uple. */
  implicit def tuple22[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder, A19: CellDecoder,
  A20: CellDecoder, A21: CellDecoder, A22: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12,
    A13, A14, A15, A16, A17, A18, A19, A20, A21, A22)] = decoder22(Tuple22.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9,
    A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
    15, 16, 17, 18, 19, 20, 21)
}