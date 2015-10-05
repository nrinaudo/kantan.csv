package com.nrinaudo.csv

import simulacrum.{noop, typeclass}

import scala.collection.generic.CanBuildFrom

/** Type class for decoding the content of a CSV row.
  *
  * Default implementations are provided in the companion object.
  */
@typeclass trait RowDecoder[A] { self =>
  @noop def decode(row: Seq[String]): DecodeResult[A]
  @noop def map[B](f: A => B): RowDecoder[B] = RowDecoder(ss => decode(ss).map(f))
}

object RowDecoder {
  def apply[A](f: Seq[String] => DecodeResult[A]): RowDecoder[A] = new RowDecoder[A] {
    override def decode(row: Seq[String]) = f(row)
  }

  implicit val stringSeq: RowDecoder[Seq[String]] = RowDecoder(ss => DecodeResult.success(ss))

  implicit def either[A: RowDecoder, B: RowDecoder]: RowDecoder[Either[A, B]] = RowDecoder { ss =>
    RowDecoder[A].decode(ss).map(a => Left(a): Either[A, B]).orElse(RowDecoder[B].decode(ss).map(b => Right(b): Either[A, B]))
  }

  /** Generic `RowDecoder` for collections. */
  implicit def collection[A: CellDecoder, M[X]](implicit cbf: CanBuildFrom[Nothing, A, M[A]]): RowDecoder[M[A]] =
    RowDecoder(ss => ss.foldLeft(DecodeResult.success(cbf.apply())) { (racc, s) => for {
      acc <- racc
      a   <- CellDecoder[A].decode(s)
    } yield acc += a
    }.map(_.result()))


  // - Case class decoders ---------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // I am not proud of this, but I don't know of any other way to deal with non "curryable" types.

  /** Helper function to reduce the amount of boilerplate required by dealing with case classes. */
  @inline private def r[A: CellDecoder](ss: Seq[String], index: Int): DecodeResult[A] = CellDecoder[A].decode(ss, index)

  def caseDecoder1[A0: CellDecoder, R](f: (A0) => R): RowDecoder[R] = RowDecoder(ss => r[A0](ss, 0).map(f))

  def caseDecoder2[A0: CellDecoder, A1: CellDecoder, R](f: (A0, A1) => R)(i0: Int, i1: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1)) yield f(f0, f1))

  def caseDecoder3[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, R]
    (f: (A0, A1, A2) => R)(i0: Int, i1: Int, i2: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2)) yield f(f0, f1, f2))

  def caseDecoder4[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, R]
  (f: (A0, A1, A2, A3) => R)(i0: Int, i1: Int, i2: Int, i3: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3)) yield
    f(f0, f1, f2, f3))

  def caseDecoder5[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4) => R)(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                        f4 <- r[A4](ss, i4)) yield f(f0, f1, f2, f3, f4))

  def caseDecoder6[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5) => R)(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                        f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5)) yield f(f0, f1, f2, f3, f4, f5))

  def caseDecoder7[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, R](f: (A0, A1, A2, A3, A4, A5, A6) => R)
                    (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                        f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6)) yield
    f(f0, f1, f2, f3, f4, f5, f6))

  def caseDecoder8[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                        f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7)) yield
    f(f0, f1, f2, f3, f4, f5, f6, f7))

  def caseDecoder9[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                        f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                        f8 <- r[A8](ss, i8)) yield f(f0, f1, f2, f3, f4, f5, f6, f7, f8))

  def caseDecoder10[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                        f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                        f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9))
      yield f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9))

  def caseDecoder11[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                        f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                        f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10))
      yield f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10))

  def caseDecoder12[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int,
   i10: Int, i11: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                        f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                        f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11))
      yield f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11))

  def caseDecoder13[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int,
   i10: Int, i11: Int, i12: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                        f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                        f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                        f12 <- r[A12](ss, i12)) yield
    f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12))

  def caseDecoder14[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
  A6: CellDecoder, A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder,
  A13: CellDecoder, R](f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13) => R)
                     (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int,
                      i10: Int, i11: Int, i12: Int, i13: Int): RowDecoder[R] =
    RowDecoder(ss => for(f0 <- r[A0](ss, i0); f1 <- r[A1](ss, i1); f2 <- r[A2](ss, i2); f3 <- r[A3](ss, i3);
                        f4 <- r[A4](ss, i4); f5 <- r[A5](ss, i5); f6 <- r[A6](ss, i6); f7 <- r[A7](ss, i7);
                        f8 <- r[A8](ss, i8); f9 <- r[A9](ss, i9); f10 <- r[A10](ss, i10); f11 <- r[A11](ss, i11);
                        f12 <- r[A12](ss, i12); f13 <- r[A13](ss, i13)) yield
    f(f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13))

  def caseDecoder15[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
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

  def caseDecoder16[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
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

  def caseDecoder17[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
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

  def caseDecoder18[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
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

  def caseDecoder19[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
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

  def caseDecoder20[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
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

  def caseDecoder21[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
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

  def caseDecoder22[A0: CellDecoder, A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder,
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
  implicit def tuple1[A1: CellDecoder]: RowDecoder[Tuple1[A1]] =
    caseDecoder1(Tuple1.apply[A1])

  implicit def tuple2[A1: CellDecoder, A2: CellDecoder]: RowDecoder[(A1, A2)] =
    caseDecoder2(Tuple2.apply[A1, A2])(0, 1)

  implicit def tuple3[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder]: RowDecoder[(A1, A2, A3)] =
    caseDecoder3(Tuple3.apply[A1, A2, A3])(0, 1, 2)

  implicit def tuple4[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder]: RowDecoder[(A1, A2, A3, A4)] =
    caseDecoder4(Tuple4.apply[A1, A2, A3, A4])(0, 1, 2, 3)

  implicit def tuple5[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5)] = caseDecoder5(Tuple5.apply[A1, A2, A3, A4, A5])(0, 1, 2, 3, 4)

  implicit def tuple6[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6)] = caseDecoder6(Tuple6.apply[A1, A2, A3, A4, A5, A6])(0, 1, 2, 3, 4, 5)

  implicit def tuple7[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7)] =
    caseDecoder7(Tuple7.apply[A1, A2, A3, A4, A5, A6, A7])(0, 1, 2, 3, 4, 5, 6)

  implicit def tuple8[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8)] =
    caseDecoder8(Tuple8.apply[A1, A2, A3, A4, A5, A6, A7, A8])(0, 1, 2, 3, 4, 5, 6, 7)

  implicit def tuple9[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9)] =
    caseDecoder9(Tuple9.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9])(0, 1, 2, 3, 4, 5, 6, 7, 8)

  implicit def tuple10[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)] =
    caseDecoder10(Tuple10.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

  implicit def tuple11[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)] =
    caseDecoder11(Tuple11.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

  implicit def tuple12[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)] =
    caseDecoder12(Tuple12.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

  implicit def tuple13[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)] =
    caseDecoder13(Tuple13.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
      10, 11, 12)

  implicit def tuple14[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)] =
    caseDecoder14(Tuple14.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14])(0, 1, 2, 3, 4, 5, 6, 7, 8,
      9, 10, 11, 12, 13)

  implicit def tuple15[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)] =
    caseDecoder15(Tuple15.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12, 13, 14)

  implicit def tuple16[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)] =
    caseDecoder16(Tuple16.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16])(0, 1, 2, 3, 4,
      5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

  implicit def tuple17[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)] =
    caseDecoder17(Tuple17.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17])(0, 1, 2, 3,
      4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)

  implicit def tuple18[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)] =
    caseDecoder18(Tuple18.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18])(0, 1,
      2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)

  implicit def tuple19[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder, A19: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19)] =
    caseDecoder19(Tuple19.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18,
      A19])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)

  implicit def tuple20[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder, A19: CellDecoder, A20: CellDecoder]:
  RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20)] =
    caseDecoder20(Tuple20.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19,
      A20])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)

  implicit def tuple21[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder, A19: CellDecoder,
  A20: CellDecoder, A21: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16,
    A17, A18, A19, A20, A21)] = caseDecoder21(Tuple21.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
      15, 16, 17, 18, 19, 20)

  implicit def tuple22[A1: CellDecoder, A2: CellDecoder, A3: CellDecoder, A4: CellDecoder, A5: CellDecoder, A6: CellDecoder,
  A7: CellDecoder, A8: CellDecoder, A9: CellDecoder, A10: CellDecoder, A11: CellDecoder, A12: CellDecoder, A13: CellDecoder,
  A14: CellDecoder, A15: CellDecoder, A16: CellDecoder, A17: CellDecoder, A18: CellDecoder, A19: CellDecoder,
  A20: CellDecoder, A21: CellDecoder, A22: CellDecoder]: RowDecoder[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12,
    A13, A14, A15, A16, A17, A18, A19, A20, A21, A22)] = caseDecoder22(Tuple22.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9,
    A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
      15, 16, 17, 18, 19, 20, 21)
}