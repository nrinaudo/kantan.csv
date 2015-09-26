package com.nrinaudo.csv

import simulacrum.typeclass

import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.ArrayBuffer

/** Typeclass for reading the content of a CSV row.
  *
  * Default implementations are provided in the companion object.
  */
@typeclass trait RowReader[A] { self =>
  def read(row: ArrayBuffer[String]): A
  def hasHeader: Boolean = false

  def map[B](f: A => B): RowReader[B] = RowReader(ss => f(read(ss)))
  def skipHeader(h: Boolean): RowReader[A] = RowReader(h, read _)
}

object RowReader {
  def apply[A](f: ArrayBuffer[String] => A): RowReader[A] = apply(false, f)

  private def apply[A](h: Boolean, f: ArrayBuffer[String] => A): RowReader[A] = new RowReader[A] {
    override def read(row: ArrayBuffer[String]) = f(row)
    override def hasHeader = h
  }

  /** Generic {{{RowReader}}} for collections. */
  implicit def collection[A: CellReader, M[X]](implicit cbf: CanBuildFrom[Nothing, A, M[A]]): RowReader[M[A]] = apply { ss =>
    ss.foldLeft(cbf.apply()) { (acc, s) => acc += CellReader[A].read(s) }.result()
  }


  // - Case class readers ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  // I am not proud of this, but I don't know of any other way to deal with non "curryable" types.

  /** Helper function to reduce the amount of boilerplate required by dealing with case classes. */
  @inline private def r[A: CellReader](ss: ArrayBuffer[String], index: Int) = CellReader[A].read(ss(index))

  def caseReader1[A0: CellReader, R]
      (f: (A0) => R): RowReader[R] = apply(ss => f(r[A0](ss, 0)))

  def caseReader2[A0: CellReader, A1: CellReader, R]
    (f: (A0, A1) => R)(i0: Int, i1: Int): RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1)))

  def caseReader3[A0: CellReader, A1: CellReader, A2: CellReader, R]
  (f: (A0, A1, A2) => R)(i0: Int, i1: Int, i2: Int): RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2)))

  def caseReader4[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, R]
  (f: (A0, A1, A2, A3) => R)(i0: Int, i1: Int, i2: Int, i3: Int):
  RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3)))

  def caseReader5[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, R]
    (f: (A0, A1, A2, A3, A4) => R)(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int):
    RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4)))

  def caseReader6[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5) => R)(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int):
  RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5)))

  def caseReader7[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, R](f: (A0, A1, A2, A3, A4, A5, A6) => R)(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int):
  RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5),
    r[A6](ss, i6)))

  def caseReader8[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
    A6: CellReader, A7: CellReader, R](f: (A0, A1, A2, A3, A4, A5, A6, A7) => R)
    (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int):
    RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5),
      r[A6](ss, i6), r[A7](ss, i7)))

  def caseReader9[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, R](f: (A0, A1, A2, A3, A4, A5, A6, A7, A8) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int):
  RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5),
    r[A6](ss, i6), r[A7](ss, i7), r[A8](ss, i8)))

  def caseReader10[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int):
  RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5),
    r[A6](ss, i6), r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9)))

  def caseReader11[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int):
  RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5),
    r[A6](ss, i6), r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10)))

  def caseReader12[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int):
  RowReader[R] = apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5),
    r[A6](ss, i6), r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11)))


  def caseReader13[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int): RowReader[R] =
    apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5), r[A6](ss, i6),
      r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11), r[A12](ss, i12)))

  def caseReader14[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader,
  A13: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int): RowReader[R] =
    apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5), r[A6](ss, i6),
      r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11), r[A12](ss, i12), r[A13](ss, i13)))

  def caseReader15[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader,
  A13: CellReader, A14: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int): RowReader[R] =
    apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5), r[A6](ss, i6),
      r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11), r[A12](ss, i12), r[A13](ss, i13),
      r[A14](ss, i14)))

  def caseReader16[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader,
  A13: CellReader, A14: CellReader, A15: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int): RowReader[R] =
    apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5), r[A6](ss, i6),
      r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11), r[A12](ss, i12), r[A13](ss, i13),
      r[A14](ss, i14), r[A15](ss, i15)))

  def caseReader17[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader,
  A13: CellReader, A14: CellReader, A15: CellReader, A16: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int): RowReader[R] =
    apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5), r[A6](ss, i6),
      r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11), r[A12](ss, i12), r[A13](ss, i13),
      r[A14](ss, i14),r[A15](ss, i15), r[A16](ss, i16)))

  def caseReader18[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader,
  A13: CellReader, A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int): RowReader[R] =
    apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5), r[A6](ss, i6),
      r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11), r[A12](ss, i12), r[A13](ss, i13),
      r[A14](ss, i14),r[A15](ss, i15), r[A16](ss, i16), r[A17](ss, i17)))

  def caseReader19[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader,
  A13: CellReader, A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int): RowReader[R] =
    apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5), r[A6](ss, i6),
      r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11), r[A12](ss, i12), r[A13](ss, i13),
      r[A14](ss, i14),r[A15](ss, i15), r[A16](ss, i16), r[A17](ss, i17), r[A18](ss, i18)))

  def caseReader20[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader,
  A13: CellReader, A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader,
  A19: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int): RowReader[R] =
    apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5), r[A6](ss, i6),
      r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11), r[A12](ss, i12), r[A13](ss, i13),
      r[A14](ss, i14),r[A15](ss, i15), r[A16](ss, i16), r[A17](ss, i17), r[A18](ss, i18), r[A19](ss, i19)))

  def caseReader21[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader,
  A13: CellReader, A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader,
  A19: CellReader, A20: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int): RowReader[R] =
    apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5), r[A6](ss, i6),
      r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11), r[A12](ss, i12), r[A13](ss, i13),
      r[A14](ss, i14),r[A15](ss, i15), r[A16](ss, i16), r[A17](ss, i17), r[A18](ss, i18), r[A19](ss, i19),
      r[A20](ss, i20)))

  def caseReader22[A0: CellReader, A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader,
  A6: CellReader, A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader,
  A13: CellReader, A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader,
  A19: CellReader, A20: CellReader, A21: CellReader, R]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21) => R)
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int, i21: Int): RowReader[R] =
    apply(ss => f(r[A0](ss, i0), r[A1](ss, i1), r[A2](ss, i2), r[A3](ss, i3), r[A4](ss, i4), r[A5](ss, i5), r[A6](ss, i6),
      r[A7](ss, i7), r[A8](ss, i8), r[A9](ss, i9), r[A10](ss, i10), r[A11](ss, i11), r[A12](ss, i12), r[A13](ss, i13),
      r[A14](ss, i14),r[A15](ss, i15), r[A16](ss, i16), r[A17](ss, i17), r[A18](ss, i18), r[A19](ss, i19),
      r[A20](ss, i20), r[A21](ss, 20)))


  // - Tuple readers ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def tuple1[A1: CellReader]: RowReader[Tuple1[A1]] =
      caseReader1(Tuple1.apply[A1])

  implicit def tuple2[A1: CellReader, A2: CellReader]: RowReader[(A1, A2)] =
    caseReader2(Tuple2.apply[A1, A2])(0, 1)

  implicit def tuple3[A1: CellReader, A2: CellReader, A3: CellReader]: RowReader[(A1, A2, A3)] =
    caseReader3(Tuple3.apply[A1, A2, A3])(0, 1, 2)

  implicit def tuple4[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader]: RowReader[(A1, A2, A3, A4)] =
    caseReader4(Tuple4.apply[A1, A2, A3, A4])(0, 1, 2, 3)

  implicit def tuple5[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader]:
  RowReader[(A1, A2, A3, A4, A5)] = caseReader5(Tuple5.apply[A1, A2, A3, A4, A5])(0, 1, 2, 3, 4)

  implicit def tuple6[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6)] = caseReader6(Tuple6.apply[A1, A2, A3, A4, A5, A6])(0, 1, 2, 3, 4, 5)

  implicit def tuple7[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7)] =
    caseReader7(Tuple7.apply[A1, A2, A3, A4, A5, A6, A7])(0, 1, 2, 3, 4, 5, 6)

  implicit def tuple8[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8)] =
    caseReader8(Tuple8.apply[A1, A2, A3, A4, A5, A6, A7, A8])(0, 1, 2, 3, 4, 5, 6, 7)

  implicit def tuple9[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9)] =
    caseReader9(Tuple9.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9])(0, 1, 2, 3, 4, 5, 6, 7, 8)

  implicit def tuple10[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)] =
    caseReader10(Tuple10.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

  implicit def tuple11[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)] =
    caseReader11(Tuple11.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

  implicit def tuple12[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)] =
    caseReader12(Tuple12.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

  implicit def tuple13[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)] =
    caseReader13(Tuple13.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
      10, 11, 12)

  implicit def tuple14[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)] =
    caseReader14(Tuple14.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14])(0, 1, 2, 3, 4, 5, 6, 7, 8,
      9, 10, 11, 12, 13)

  implicit def tuple15[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)] =
    caseReader15(Tuple15.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12, 13, 14)

  implicit def tuple16[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)] =
    caseReader16(Tuple16.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16])(0, 1, 2, 3, 4,
      5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

  implicit def tuple17[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)] =
    caseReader17(Tuple17.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17])(0, 1, 2, 3,
      4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)

  implicit def tuple18[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)] =
    caseReader18(Tuple18.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18])(0, 1,
      2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)

  implicit def tuple19[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19)] =
    caseReader19(Tuple19.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18,
      A19])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)

  implicit def tuple20[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader, A20: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20)] =
    caseReader20(Tuple20.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19,
      A20])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)

  implicit def tuple21[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader,
  A20: CellReader, A21: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16,
    A17, A18, A19, A20, A21)] = caseReader21(Tuple21.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
          15, 16, 17, 18, 19, 20)

  implicit def tuple22[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader,
  A20: CellReader, A21: CellReader, A22: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12,
    A13, A14, A15, A16, A17, A18, A19, A20, A21, A22)] = caseReader22(Tuple22.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9,
    A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
      15, 16, 17, 18, 19, 20, 21)
}