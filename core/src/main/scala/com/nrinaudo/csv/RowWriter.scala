package com.nrinaudo.csv

import simulacrum.{op, noop, typeclass}

/** Typeclass used to turn instances of {{{A}}} into a CSV row.
  *
  * Note that the companion object has helpful functions for deriving instances by combining [[CellEncoder]]s.
  */
@typeclass trait RowWriter[A] { self =>
  @op("asCsvRow") def write(a: A): Seq[String]
  @noop def contramap[B](f: B => A): RowWriter[B] = RowWriter(f andThen write _)
}

object RowWriter {
  import ops._
  import CellEncoder.ops._

  def apply[A](f: A => Seq[String]): RowWriter[A] = new RowWriter[A] {
    override def write(a: A) = f(a)
  }

  /** Specialised writer for sequences of strings: these do not need to be modified. */
  implicit def strSeq[M[X] <: Seq[X]]: RowWriter[M[String]] = RowWriter(ss => ss)

  implicit def either[A: RowWriter, B: RowWriter]: RowWriter[Either[A, B]] = RowWriter { ss => ss match {
    case Left(a) => a.asCsvRow
    case Right(b) => b.asCsvRow
  }}


  implicit def traversable[A: CellEncoder, M[X] <: TraversableOnce[X]]: RowWriter[M[A]] = RowWriter { as =>
    as.foldLeft(Seq.newBuilder[String])((acc, a) => acc += a.asCsvCell).result()
  }

  @inline private def w[A: CellEncoder](a: A): String = a.asCsvCell

  def caseWriter1[C, A0: CellEncoder](f: C => Option[A0]): RowWriter[C] =
    RowWriter(a => List(w(f(a).get)))

  def caseWriter2[C, A0: CellEncoder, A1: CellEncoder](f: C => Option[(A0, A1)])
                                                    (i0: Int, i1: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](2)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest.toSeq
    }

  def caseWriter3[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder](f: C => Option[(A0, A1, A2)])
                                                                    (i0: Int, i1: Int, i2: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](3)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest.toSeq
    }

  def caseWriter4[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3)])(i0: Int, i1: Int, i2: Int, i3: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](4)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest.toSeq
    }

  def caseWriter5[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](5)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest.toSeq
    }

  def caseWriter6[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](6)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest.toSeq
    }

  def caseWriter7[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](7)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest.toSeq
    }

  def caseWriter8[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](8)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest.toSeq
    }

  def caseWriter9[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](9)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest.toSeq
    }

  def caseWriter10[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](10)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest.toSeq
    }

  def caseWriter11[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](11)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest.toSeq
    }

  def caseWriter12[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int):
  RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](12)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest.toSeq
    }

  def caseWriter13[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](13)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest(i12) = e._13.asCsvCell
      dest.toSeq
    }

  def caseWriter14[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](14)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest(i12) = e._13.asCsvCell
      dest(i13) = e._14.asCsvCell
      dest.toSeq
    }

  def caseWriter15[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](15)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest(i12) = e._13.asCsvCell
      dest(i13) = e._14.asCsvCell
      dest(i14) = e._15.asCsvCell
      dest.toSeq
    }

  def caseWriter16[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](16)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest(i12) = e._13.asCsvCell
      dest(i13) = e._14.asCsvCell
      dest(i14) = e._15.asCsvCell
      dest(i15) = e._16.asCsvCell
      dest.toSeq
    }

  def caseWriter17[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](17)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest(i12) = e._13.asCsvCell
      dest(i13) = e._14.asCsvCell
      dest(i14) = e._15.asCsvCell
      dest(i15) = e._16.asCsvCell
      dest(i16) = e._17.asCsvCell
      dest.toSeq
    }

  def caseWriter18[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](18)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest(i12) = e._13.asCsvCell
      dest(i13) = e._14.asCsvCell
      dest(i14) = e._15.asCsvCell
      dest(i15) = e._16.asCsvCell
      dest(i16) = e._17.asCsvCell
      dest(i17) = e._18.asCsvCell
      dest.toSeq
    }

  def caseWriter19[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                          i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                          i17: Int, i18: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](19)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest(i12) = e._13.asCsvCell
      dest(i13) = e._14.asCsvCell
      dest(i14) = e._15.asCsvCell
      dest(i15) = e._16.asCsvCell
      dest(i16) = e._17.asCsvCell
      dest(i17) = e._18.asCsvCell
      dest(i18) = e._19.asCsvCell
      dest.toSeq
    }

  def caseWriter20[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder,
  A19: CellEncoder](f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                               i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                               i17: Int, i18: Int, i19: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](20)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest(i12) = e._13.asCsvCell
      dest(i13) = e._14.asCsvCell
      dest(i14) = e._15.asCsvCell
      dest(i15) = e._16.asCsvCell
      dest(i16) = e._17.asCsvCell
      dest(i17) = e._18.asCsvCell
      dest(i18) = e._19.asCsvCell
      dest(i19) = e._20.asCsvCell
      dest.toSeq
    }

  def caseWriter21[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder](f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                                    i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                                    i17: Int, i18: Int, i19: Int, i20: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](21)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest(i12) = e._13.asCsvCell
      dest(i13) = e._14.asCsvCell
      dest(i14) = e._15.asCsvCell
      dest(i15) = e._16.asCsvCell
      dest(i16) = e._17.asCsvCell
      dest(i17) = e._18.asCsvCell
      dest(i18) = e._19.asCsvCell
      dest(i19) = e._20.asCsvCell
      dest(i20) = e._21.asCsvCell
      dest.toSeq
    }

  def caseWriter22[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder, A21: CellEncoder](f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                                         i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                                         i17: Int, i18: Int, i19: Int, i20: Int, i21: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](22)

      dest(i0) = e._1.asCsvCell
      dest(i1) = e._2.asCsvCell
      dest(i2) = e._3.asCsvCell
      dest(i3) = e._4.asCsvCell
      dest(i4) = e._5.asCsvCell
      dest(i5) = e._6.asCsvCell
      dest(i6) = e._7.asCsvCell
      dest(i7) = e._8.asCsvCell
      dest(i8) = e._9.asCsvCell
      dest(i9) = e._10.asCsvCell
      dest(i10) = e._11.asCsvCell
      dest(i11) = e._12.asCsvCell
      dest(i12) = e._13.asCsvCell
      dest(i13) = e._14.asCsvCell
      dest(i14) = e._15.asCsvCell
      dest(i15) = e._16.asCsvCell
      dest(i16) = e._17.asCsvCell
      dest(i17) = e._18.asCsvCell
      dest(i18) = e._19.asCsvCell
      dest(i19) = e._20.asCsvCell
      dest(i20) = e._21.asCsvCell
      dest(i21) = e._22.asCsvCell
      dest.toSeq
    }

  implicit def tuple1[A0: CellEncoder]: RowWriter[Tuple1[A0]] =
      caseWriter1(Tuple1.unapply[A0])

  implicit def tuple2[A0: CellEncoder, A1: CellEncoder]: RowWriter[(A0, A1)] =
    caseWriter2(Tuple2.unapply[A0, A1])(0, 1)

  implicit def tuple3[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder]: RowWriter[(A0, A1, A2)] =
    caseWriter3(Tuple3.unapply[A0, A1, A2])(0, 1, 2)

  implicit def tuple4[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder]:
  RowWriter[(A0, A1, A2, A3)] = caseWriter4(Tuple4.unapply[A0, A1, A2, A3])(0, 1, 2, 3)

  implicit def tuple5[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder]:
  RowWriter[(A0, A1, A2, A3, A4)] = caseWriter5(Tuple5.unapply[A0, A1, A2, A3, A4])(0, 1, 2, 3, 4)

  implicit def tuple6[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder]:
  RowWriter[(A0, A1, A2, A3, A4, A5)] =
    caseWriter6(Tuple6.unapply[A0, A1, A2, A3, A4, A5])(0, 1, 2, 3, 4, 5)

  implicit def tuple7[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6)] =
    caseWriter7(Tuple7.unapply[A0, A1, A2, A3, A4, A5, A6])(0, 1, 2, 3, 4, 5, 6)

  implicit def tuple8[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7)] =
    caseWriter8(Tuple8.unapply[A0, A1, A2, A3, A4, A5, A6, A7])(0, 1, 2, 3, 4, 5, 6, 7)

  implicit def tuple9[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8)] =
    caseWriter9(Tuple9.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8])(0, 1, 2, 3, 4, 5, 6,7, 8)

  implicit def tuple10[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8,
    A9)] = caseWriter10(Tuple10.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9])(0, 1, 2, 3, 4, 5, 6,7, 8, 9)

  implicit def tuple11[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5,
    A6, A7, A8, A9, A10)] =
    caseWriter11(Tuple11.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10])(0, 1, 2, 3, 4, 5, 6,7, 8, 9, 10)

  implicit def tuple12[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder]: RowWriter[(A0,
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)] =
    caseWriter12(Tuple12.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11])(0, 1, 2, 3, 4, 5, 6,7, 8, 9, 10, 11)

  implicit def tuple13[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder]:
  RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)] =
    caseWriter13(Tuple13.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12)

  implicit def tuple14[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)] =
    caseWriter14(Tuple14.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12, 13)

  implicit def tuple15[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)] =
    caseWriter15(Tuple15.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12, 13, 14)

  implicit def tuple16[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12,
    A13, A14, A15)] = caseWriter16(Tuple16.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

  implicit def tuple17[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8,
    A9, A10, A11, A12, A13, A14, A15, A16)] =
    caseWriter17(Tuple17.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16])(0, 1,
      2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)

  implicit def tuple18[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4,
    A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)] =
    caseWriter18(Tuple18.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17])(0, 1,
      2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)

  implicit def tuple19[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder]: RowWriter[(A0,
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)] =
    caseWriter19(Tuple19.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
      A12, A13, A14, A15, A16, A17, A18])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)

  implicit def tuple20[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder,
  A19: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18,
    A19)] = caseWriter20(Tuple20.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
    A12, A13, A14, A15, A16, A17, A18, A19])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
      19)

  implicit def tuple21[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19,
    A20)] = caseWriter21(Tuple21.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
    A12, A13, A14, A15, A16, A17, A18, A19, A20])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
      19, 20)

  implicit def tuple22[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder, A21: CellEncoder]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21)] = caseWriter22(Tuple22.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
    A12, A13, A14, A15, A16, A17, A18, A19, A20, A21])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
      19, 20, 21)
}