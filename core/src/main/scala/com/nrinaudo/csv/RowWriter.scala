package com.nrinaudo.csv

import simulacrum.{noop, typeclass}

/** Typeclass used to turn instances of {{{A}}} into a CSV row.
  *
  * Note that the companion object has helpful functions for deriving instances by combining [[CellWriter]]s.
  */
@typeclass trait RowWriter[A] { self =>
  def write(a: A): Seq[String]
  @noop def contramap[B](f: B => A): RowWriter[B] = RowWriter(f andThen write _)
}

object RowWriter {
  def apply[A](f: A => Seq[String]): RowWriter[A] = new RowWriter[A] {
    override def write(a: A) = f(a)
  }

  /** Specialised writer for sequences of strings: these do not need to be modified. */
  implicit def strSeq[M[X] <: Seq[X]]: RowWriter[M[String]] = RowWriter(ss => ss)

  implicit def either[A: RowWriter, B: RowWriter]: RowWriter[Either[A, B]] = RowWriter { ss => ss match {
    case Left(a) => RowWriter[A].write(a)
    case Right(b) => RowWriter[B].write(b)
  }}


  implicit def traversable[A: CellWriter, M[X] <: TraversableOnce[X]]: RowWriter[M[A]] = RowWriter { as =>
    as.foldLeft(Seq.newBuilder[String]) { (acc, s) => acc += CellWriter[A].write(s) }.result()
  }

  @inline private def w[A: CellWriter](a: A): String = CellWriter[A].write(a)

  def caseWriter1[C, A0: CellWriter](f: C => Option[A0]): RowWriter[C] =
    RowWriter(a => List(w(f(a).get)))

  def caseWriter2[C, A0: CellWriter, A1: CellWriter](f: C => Option[(A0, A1)])
                                                    (i0: Int, i1: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](2)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest.toSeq
    }

  def caseWriter3[C, A0: CellWriter, A1: CellWriter, A2: CellWriter](f: C => Option[(A0, A1, A2)])
                                                                    (i0: Int, i1: Int, i2: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](3)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest.toSeq
    }

  def caseWriter4[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter]
  (f: C => Option[(A0, A1, A2, A3)])(i0: Int, i1: Int, i2: Int, i3: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](4)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest.toSeq
    }

  def caseWriter5[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](5)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest.toSeq
    }

  def caseWriter6[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](6)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest.toSeq
    }

  def caseWriter7[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](7)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest.toSeq
    }

  def caseWriter8[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](8)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest.toSeq
    }

  def caseWriter9[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](9)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest.toSeq
    }

  def caseWriter10[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](10)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest.toSeq
    }

  def caseWriter11[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](11)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest.toSeq
    }

  def caseWriter12[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int):
  RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](12)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest.toSeq
    }

  def caseWriter13[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](13)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest(i12) = CellWriter[A12].write(e._13)
      dest.toSeq
    }

  def caseWriter14[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](14)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest(i12) = CellWriter[A12].write(e._13)
      dest(i13) = CellWriter[A13].write(e._14)
      dest.toSeq
    }

  def caseWriter15[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](15)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest(i12) = CellWriter[A12].write(e._13)
      dest(i13) = CellWriter[A13].write(e._14)
      dest(i14) = CellWriter[A14].write(e._15)
      dest.toSeq
    }

  def caseWriter16[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](16)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest(i12) = CellWriter[A12].write(e._13)
      dest(i13) = CellWriter[A13].write(e._14)
      dest(i14) = CellWriter[A14].write(e._15)
      dest(i15) = CellWriter[A15].write(e._16)
      dest.toSeq
    }

  def caseWriter17[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](17)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest(i12) = CellWriter[A12].write(e._13)
      dest(i13) = CellWriter[A13].write(e._14)
      dest(i14) = CellWriter[A14].write(e._15)
      dest(i15) = CellWriter[A15].write(e._16)
      dest(i16) = CellWriter[A16].write(e._17)
      dest.toSeq
    }

  def caseWriter18[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter, A17: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](18)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest(i12) = CellWriter[A12].write(e._13)
      dest(i13) = CellWriter[A13].write(e._14)
      dest(i14) = CellWriter[A14].write(e._15)
      dest(i15) = CellWriter[A15].write(e._16)
      dest(i16) = CellWriter[A16].write(e._17)
      dest(i17) = CellWriter[A17].write(e._18)
      dest.toSeq
    }

  def caseWriter19[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter, A17: CellWriter, A18: CellWriter]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                          i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                          i17: Int, i18: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](19)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest(i12) = CellWriter[A12].write(e._13)
      dest(i13) = CellWriter[A13].write(e._14)
      dest(i14) = CellWriter[A14].write(e._15)
      dest(i15) = CellWriter[A15].write(e._16)
      dest(i16) = CellWriter[A16].write(e._17)
      dest(i17) = CellWriter[A17].write(e._18)
      dest(i18) = CellWriter[A18].write(e._19)
      dest.toSeq
    }

  def caseWriter20[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter, A17: CellWriter, A18: CellWriter,
  A19: CellWriter](f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                               i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                               i17: Int, i18: Int, i19: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](20)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest(i12) = CellWriter[A12].write(e._13)
      dest(i13) = CellWriter[A13].write(e._14)
      dest(i14) = CellWriter[A14].write(e._15)
      dest(i15) = CellWriter[A15].write(e._16)
      dest(i16) = CellWriter[A16].write(e._17)
      dest(i17) = CellWriter[A17].write(e._18)
      dest(i18) = CellWriter[A18].write(e._19)
      dest(i19) = CellWriter[A19].write(e._20)
      dest.toSeq
    }

  def caseWriter21[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter, A17: CellWriter, A18: CellWriter, A19: CellWriter,
  A20: CellWriter](f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                                    i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                                    i17: Int, i18: Int, i19: Int, i20: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](21)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest(i12) = CellWriter[A12].write(e._13)
      dest(i13) = CellWriter[A13].write(e._14)
      dest(i14) = CellWriter[A14].write(e._15)
      dest(i15) = CellWriter[A15].write(e._16)
      dest(i16) = CellWriter[A16].write(e._17)
      dest(i17) = CellWriter[A17].write(e._18)
      dest(i18) = CellWriter[A18].write(e._19)
      dest(i19) = CellWriter[A19].write(e._20)
      dest(i20) = CellWriter[A20].write(e._21)
      dest.toSeq
    }

  def caseWriter22[C, A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter, A17: CellWriter, A18: CellWriter, A19: CellWriter,
  A20: CellWriter, A21: CellWriter](f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                                         i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                                         i17: Int, i18: Int, i19: Int, i20: Int, i21: Int): RowWriter[C] =
    RowWriter { a =>
      val e = f(a).get
      val dest = new Array[String](22)

      dest(i0) = CellWriter[A0].write(e._1)
      dest(i1) = CellWriter[A1].write(e._2)
      dest(i2) = CellWriter[A2].write(e._3)
      dest(i3) = CellWriter[A3].write(e._4)
      dest(i4) = CellWriter[A4].write(e._5)
      dest(i5) = CellWriter[A5].write(e._6)
      dest(i6) = CellWriter[A6].write(e._7)
      dest(i7) = CellWriter[A7].write(e._8)
      dest(i8) = CellWriter[A8].write(e._9)
      dest(i9) = CellWriter[A9].write(e._10)
      dest(i10) = CellWriter[A10].write(e._11)
      dest(i11) = CellWriter[A11].write(e._12)
      dest(i12) = CellWriter[A12].write(e._13)
      dest(i13) = CellWriter[A13].write(e._14)
      dest(i14) = CellWriter[A14].write(e._15)
      dest(i15) = CellWriter[A15].write(e._16)
      dest(i16) = CellWriter[A16].write(e._17)
      dest(i17) = CellWriter[A17].write(e._18)
      dest(i18) = CellWriter[A18].write(e._19)
      dest(i19) = CellWriter[A19].write(e._20)
      dest(i20) = CellWriter[A20].write(e._21)
      dest(i21) = CellWriter[A21].write(e._22)
      dest.toSeq
    }

  implicit def tuple1[A0: CellWriter]: RowWriter[Tuple1[A0]] =
      caseWriter1(Tuple1.unapply[A0])

  implicit def tuple2[A0: CellWriter, A1: CellWriter]: RowWriter[(A0, A1)] =
    caseWriter2(Tuple2.unapply[A0, A1])(0, 1)

  implicit def tuple3[A0: CellWriter, A1: CellWriter, A2: CellWriter]: RowWriter[(A0, A1, A2)] =
    caseWriter3(Tuple3.unapply[A0, A1, A2])(0, 1, 2)

  implicit def tuple4[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter]:
  RowWriter[(A0, A1, A2, A3)] = caseWriter4(Tuple4.unapply[A0, A1, A2, A3])(0, 1, 2, 3)

  implicit def tuple5[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter]:
  RowWriter[(A0, A1, A2, A3, A4)] = caseWriter5(Tuple5.unapply[A0, A1, A2, A3, A4])(0, 1, 2, 3, 4)

  implicit def tuple6[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter]:
  RowWriter[(A0, A1, A2, A3, A4, A5)] =
    caseWriter6(Tuple6.unapply[A0, A1, A2, A3, A4, A5])(0, 1, 2, 3, 4, 5)

  implicit def tuple7[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6)] =
    caseWriter7(Tuple7.unapply[A0, A1, A2, A3, A4, A5, A6])(0, 1, 2, 3, 4, 5, 6)

  implicit def tuple8[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7)] =
    caseWriter8(Tuple8.unapply[A0, A1, A2, A3, A4, A5, A6, A7])(0, 1, 2, 3, 4, 5, 6, 7)

  implicit def tuple9[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8)] =
    caseWriter9(Tuple9.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8])(0, 1, 2, 3, 4, 5, 6,7, 8)

  implicit def tuple10[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8,
    A9)] = caseWriter10(Tuple10.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9])(0, 1, 2, 3, 4, 5, 6,7, 8, 9)

  implicit def tuple11[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5,
    A6, A7, A8, A9, A10)] =
    caseWriter11(Tuple11.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10])(0, 1, 2, 3, 4, 5, 6,7, 8, 9, 10)

  implicit def tuple12[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter]: RowWriter[(A0,
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)] =
    caseWriter12(Tuple12.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11])(0, 1, 2, 3, 4, 5, 6,7, 8, 9, 10, 11)

  implicit def tuple13[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter]:
  RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)] =
    caseWriter13(Tuple13.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12)

  implicit def tuple14[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)] =
    caseWriter14(Tuple14.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12, 13)

  implicit def tuple15[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)] =
    caseWriter15(Tuple15.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12, 13, 14)

  implicit def tuple16[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12,
    A13, A14, A15)] = caseWriter16(Tuple16.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

  implicit def tuple17[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8,
    A9, A10, A11, A12, A13, A14, A15, A16)] =
    caseWriter17(Tuple17.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16])(0, 1,
      2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)

  implicit def tuple18[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter, A17: CellWriter]: RowWriter[(A0, A1, A2, A3, A4,
    A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)] =
    caseWriter18(Tuple18.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17])(0, 1,
      2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)

  implicit def tuple19[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter, A17: CellWriter, A18: CellWriter]: RowWriter[(A0,
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)] =
    caseWriter19(Tuple19.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
      A12, A13, A14, A15, A16, A17, A18])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)

  implicit def tuple20[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter, A17: CellWriter, A18: CellWriter,
  A19: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18,
    A19)] = caseWriter20(Tuple20.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
    A12, A13, A14, A15, A16, A17, A18, A19])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
      19)

  implicit def tuple21[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter, A17: CellWriter, A18: CellWriter, A19: CellWriter,
  A20: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19,
    A20)] = caseWriter21(Tuple21.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
    A12, A13, A14, A15, A16, A17, A18, A19, A20])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
      19, 20)

  implicit def tuple22[A0: CellWriter, A1: CellWriter, A2: CellWriter, A3: CellWriter, A4: CellWriter, A5: CellWriter,
  A6: CellWriter, A7: CellWriter, A8: CellWriter, A9: CellWriter, A10: CellWriter, A11: CellWriter, A12: CellWriter,
  A13: CellWriter, A14: CellWriter, A15: CellWriter, A16: CellWriter, A17: CellWriter, A18: CellWriter, A19: CellWriter,
  A20: CellWriter, A21: CellWriter]: RowWriter[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21)] = caseWriter22(Tuple22.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
    A12, A13, A14, A15, A16, A17, A18, A19, A20, A21])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
      19, 20, 21)
}