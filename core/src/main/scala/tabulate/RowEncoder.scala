package tabulate

import simulacrum.{noop, op, typeclass}

@typeclass trait RowEncoder[A] { self =>
  @op("asCsvRow") def encode(a: A): Seq[String]
  @noop def contramap[B](f: B => A): RowEncoder[B] = RowEncoder(f andThen encode _)
}

@export.imports[RowEncoder]
trait LowPriorityRowEncoders {
  implicit def traversable[A, M[X] <: TraversableOnce[X]](implicit ea: CellEncoder[A]): RowEncoder[M[A]] =
    RowEncoder { as => as.foldLeft(Seq.newBuilder[String])((acc, a) => acc += ea.encode(a)).result() }

  implicit def cellEncoder[A](implicit ea: CellEncoder[A]): RowEncoder[A] = RowEncoder(a => Seq(ea.encode(a)))
}

object RowEncoder extends LowPriorityRowEncoders {
  def apply[A](f: A => Seq[String]): RowEncoder[A] = new RowEncoder[A] {
    override def encode(a: A) = f(a)
  }

  /** Specialised encoder for sequences of strings: these do not need to be modified. */
  implicit def strSeq[M[X] <: Seq[X]]: RowEncoder[M[String]] = RowEncoder(ss => ss)

  implicit def either[A, B](implicit ea: RowEncoder[A], eb: RowEncoder[B]): RowEncoder[Either[A, B]] =
    RowEncoder { ss => ss match {
      case Left(a) => ea.encode(a)
      case Right(b) => eb.encode(b)
    }}

  implicit def option[A](implicit ea: RowEncoder[A]): RowEncoder[Option[A]] =
    RowEncoder(_.map(a => ea.encode(a)).getOrElse(Seq.empty))

  def encoder1[C, A0: CellEncoder](f: C => A0)(implicit a0: CellEncoder[A0]): RowEncoder[C] =
    RowEncoder(a => List(a0.encode(f(a))))

  def encoder2[C, A0: CellEncoder, A1: CellEncoder](f: C => (A0, A1))(i0: Int, i1: Int)
                                                   (implicit a0: CellEncoder[A0], a1: CellEncoder[A1]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(2)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest.toSeq
    }

  def encoder3[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder]
  (f: C => (A0, A1, A2))(i0: Int, i1: Int, i2: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(3)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest.toSeq
    }

  def encoder4[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder]
  (f: C => (A0, A1, A2, A3))(i0: Int, i1: Int, i2: Int, i3: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(4)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest.toSeq
    }

  def encoder5[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4]):
  RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(5)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest.toSeq
    }

  def encoder6[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(6)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest.toSeq
    }

  def encoder7[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(7)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest.toSeq
    }

  def encoder8[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(8)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest.toSeq
    }

  def encoder9[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(9)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest.toSeq
    }

  def encoder10[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(10)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest.toSeq
    }

  def encoder11[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(11)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest.toSeq
    }

  def encoder12[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(12)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest.toSeq
    }

  def encoder13[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(13)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest(i12) = a12.encode(e._13)
      dest.toSeq
    }

  def encoder14[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(14)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest(i12) = a12.encode(e._13)
      dest(i13) = a13.encode(e._14)
      dest.toSeq
    }

  def encoder15[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14]):
  RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(15)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest(i12) = a12.encode(e._13)
      dest(i13) = a13.encode(e._14)
      dest(i14) = a14.encode(e._15)
      dest.toSeq
    }

  def encoder16[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(16)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest(i12) = a12.encode(e._13)
      dest(i13) = a13.encode(e._14)
      dest(i14) = a14.encode(e._15)
      dest(i15) = a15.encode(e._16)
      dest.toSeq
    }

  def encoder17[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(17)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest(i12) = a12.encode(e._13)
      dest(i13) = a13.encode(e._14)
      dest(i14) = a14.encode(e._15)
      dest(i15) = a15.encode(e._16)
      dest(i16) = a16.encode(e._17)
      dest.toSeq
    }

  def encoder18[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(18)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest(i12) = a12.encode(e._13)
      dest(i13) = a13.encode(e._14)
      dest(i14) = a14.encode(e._15)
      dest(i15) = a15.encode(e._16)
      dest(i16) = a16.encode(e._17)
      dest(i17) = a17.encode(e._18)
      dest.toSeq
    }

  def encoder19[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(19)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest(i12) = a12.encode(e._13)
      dest(i13) = a13.encode(e._14)
      dest(i14) = a14.encode(e._15)
      dest(i15) = a15.encode(e._16)
      dest(i16) = a16.encode(e._17)
      dest(i17) = a17.encode(e._18)
      dest(i18) = a18.encode(e._19)
      dest.toSeq
    }

  def encoder20[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder,
  A19: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18], a19: CellEncoder[A19]):
  RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(20)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest(i12) = a12.encode(e._13)
      dest(i13) = a13.encode(e._14)
      dest(i14) = a14.encode(e._15)
      dest(i15) = a15.encode(e._16)
      dest(i16) = a16.encode(e._17)
      dest(i17) = a17.encode(e._18)
      dest(i18) = a18.encode(e._19)
      dest(i19) = a19.encode(e._20)
      dest.toSeq
    }

  def encoder21[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18], a19: CellEncoder[A19],
   a20: CellEncoder[A20]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(21)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest(i12) = a12.encode(e._13)
      dest(i13) = a13.encode(e._14)
      dest(i14) = a14.encode(e._15)
      dest(i15) = a15.encode(e._16)
      dest(i16) = a16.encode(e._17)
      dest(i17) = a17.encode(e._18)
      dest(i18) = a18.encode(e._19)
      dest(i19) = a19.encode(e._20)
      dest(i20) = a20.encode(e._21)

      dest.toSeq
    }

  def encoder22[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder, A21: CellEncoder]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21))
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int, i21: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18], a19: CellEncoder[A19],
   a20: CellEncoder[A20], a21: CellEncoder[A21]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      val dest = Array.fill(22)("")

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest(i7) = a7.encode(e._8)
      dest(i8) = a8.encode(e._9)
      dest(i9) = a9.encode(e._10)
      dest(i10) = a10.encode(e._11)
      dest(i11) = a11.encode(e._12)
      dest(i12) = a12.encode(e._13)
      dest(i13) = a13.encode(e._14)
      dest(i14) = a14.encode(e._15)
      dest(i15) = a15.encode(e._16)
      dest(i16) = a16.encode(e._17)
      dest(i17) = a17.encode(e._18)
      dest(i18) = a18.encode(e._19)
      dest(i19) = a19.encode(e._20)
      dest(i20) = a20.encode(e._21)
      dest(i21) = a21.encode(e._22)
      dest.toSeq
    }


  def caseEncoder1[C, A0: CellEncoder](f: C => Option[A0]): RowEncoder[C] =
    encoder1(f andThen (_.get))

  def caseEncoder2[C, A0: CellEncoder, A1: CellEncoder](f: C => Option[(A0, A1)])(i0: Int, i1: Int): RowEncoder[C] =
    encoder2(f andThen (_.get))(i0, i1)

  def caseEncoder3[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder](f: C => Option[(A0, A1, A2)])
                                                                        (i0: Int, i1: Int, i2: Int): RowEncoder[C] =
    encoder3(f andThen(_.get))(i0, i1, i2)

  def caseEncoder4[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3)])(i0: Int, i1: Int, i2: Int, i3: Int): RowEncoder[C] =
    encoder4(f andThen(_.get))(i0, i1, i2, i3)

  def caseEncoder5[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int): RowEncoder[C] =
    encoder5(f andThen(_.get))(i0, i1, i2, i3, i4)

  def caseEncoder6[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int): RowEncoder[C] =
    encoder6(f andThen(_.get))(i0, i1, i2, i3, i4, i5)

  def caseEncoder7[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int): RowEncoder[C] =
    encoder7(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6)

  def caseEncoder8[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int): RowEncoder[C] =
    encoder8(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7)

  def caseEncoder9[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int): RowEncoder[C] =
    encoder9(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8)

  def caseEncoder10[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int): RowEncoder[C] =
    encoder10(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9)

  def caseEncoder11[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int): RowEncoder[C] =
    encoder11(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10)

  def caseEncoder12[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int):
  RowEncoder[C] =
    encoder12(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11)

  def caseEncoder13[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int): RowEncoder[C] =
    encoder13(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12)

  def caseEncoder14[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int): RowEncoder[C] =
    encoder14(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13)

  def caseEncoder15[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int): RowEncoder[C] =
    encoder15(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14)

  def caseEncoder16[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int): RowEncoder[C] =
    encoder16(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15)

  def caseEncoder17[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int): RowEncoder[C] =
    encoder17(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16)

  def caseEncoder18[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int): RowEncoder[C] =
    encoder18(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17)

  def caseEncoder19[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                          i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                          i17: Int, i18: Int): RowEncoder[C] =
    encoder19(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17, i18)

  def caseEncoder20[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder,
  A19: CellEncoder](f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                               i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                               i17: Int, i18: Int, i19: Int): RowEncoder[C] =
    encoder20(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17, i18, i19)

  def caseEncoder21[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder](f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                                    i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                                    i17: Int, i18: Int, i19: Int, i20: Int): RowEncoder[C] =
    encoder21(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17, i18, i19, i20)

  def caseEncoder22[C, A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder, A21: CellEncoder](f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21)])(i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int,
                                         i9: Int, i10: Int, i11: Int, i12: Int, i13: Int, i14: Int, i15: Int, i16: Int,
                                         i17: Int, i18: Int, i19: Int, i20: Int, i21: Int): RowEncoder[C] =
    encoder22(f andThen(_.get))(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17, i18, i19, i20, i21)

  implicit def tuple1[A0: CellEncoder]: RowEncoder[Tuple1[A0]] =
    caseEncoder1(Tuple1.unapply[A0])

  implicit def tuple2[A0: CellEncoder, A1: CellEncoder]: RowEncoder[(A0, A1)] =
    caseEncoder2(Tuple2.unapply[A0, A1])(0, 1)

  implicit def tuple3[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder]: RowEncoder[(A0, A1, A2)] =
    caseEncoder3(Tuple3.unapply[A0, A1, A2])(0, 1, 2)

  implicit def tuple4[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder]:
  RowEncoder[(A0, A1, A2, A3)] = caseEncoder4(Tuple4.unapply[A0, A1, A2, A3])(0, 1, 2, 3)

  implicit def tuple5[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder]:
  RowEncoder[(A0, A1, A2, A3, A4)] = caseEncoder5(Tuple5.unapply[A0, A1, A2, A3, A4])(0, 1, 2, 3, 4)

  implicit def tuple6[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder]:
  RowEncoder[(A0, A1, A2, A3, A4, A5)] =
    caseEncoder6(Tuple6.unapply[A0, A1, A2, A3, A4, A5])(0, 1, 2, 3, 4, 5)

  implicit def tuple7[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6)] =
    caseEncoder7(Tuple7.unapply[A0, A1, A2, A3, A4, A5, A6])(0, 1, 2, 3, 4, 5, 6)

  implicit def tuple8[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7)] =
    caseEncoder8(Tuple8.unapply[A0, A1, A2, A3, A4, A5, A6, A7])(0, 1, 2, 3, 4, 5, 6, 7)

  implicit def tuple9[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8)] =
    caseEncoder9(Tuple9.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8])(0, 1, 2, 3, 4, 5, 6,7, 8)

  implicit def tuple10[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8,
    A9)] = caseEncoder10(Tuple10.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9])(0, 1, 2, 3, 4, 5, 6,7, 8, 9)

  implicit def tuple11[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5,
    A6, A7, A8, A9, A10)] =
    caseEncoder11(Tuple11.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10])(0, 1, 2, 3, 4, 5, 6,7, 8, 9, 10)

  implicit def tuple12[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder]: RowEncoder[(A0,
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)] =
    caseEncoder12(Tuple12.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11])(0, 1, 2, 3, 4, 5, 6,7, 8, 9, 10, 11)

  implicit def tuple13[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder]:
  RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)] =
    caseEncoder13(Tuple13.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12)

  implicit def tuple14[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)] =
    caseEncoder14(Tuple14.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12, 13)

  implicit def tuple15[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)] =
    caseEncoder15(Tuple15.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14])(0, 1, 2, 3, 4, 5, 6,
      7, 8, 9, 10, 11, 12, 13, 14)

  implicit def tuple16[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12,
    A13, A14, A15)] = caseEncoder16(Tuple16.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)

  implicit def tuple17[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8,
    A9, A10, A11, A12, A13, A14, A15, A16)] =
    caseEncoder17(Tuple17.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16])(0, 1,
      2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)

  implicit def tuple18[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4,
    A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)] =
    caseEncoder18(Tuple18.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17])(0, 1,
      2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)

  implicit def tuple19[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder]: RowEncoder[(A0,
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)] =
    caseEncoder19(Tuple19.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
      A12, A13, A14, A15, A16, A17, A18])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)

  implicit def tuple20[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder,
  A19: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18,
    A19)] = caseEncoder20(Tuple20.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
    A12, A13, A14, A15, A16, A17, A18, A19])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
    19)

  implicit def tuple21[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19,
    A20)] = caseEncoder21(Tuple21.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
    A12, A13, A14, A15, A16, A17, A18, A19, A20])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
    19, 20)

  implicit def tuple22[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder, A21: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21)] = caseEncoder22(Tuple22.unapply[A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11,
    A12, A13, A14, A15, A16, A17, A18, A19, A20, A21])(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
    19, 20, 21)
}