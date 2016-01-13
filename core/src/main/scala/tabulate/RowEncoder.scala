package tabulate

import simulacrum.{noop, op, typeclass}

@typeclass trait RowEncoder[A] { self =>
  @op("asCsvRow")
  def encode(a: A): Seq[String]

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

  def encoder2[C, A0: CellEncoder, A1: CellEncoder](f: C => (A0, A1))
                                                   (implicit a0: CellEncoder[A0], a1: CellEncoder[A1]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2))
    }

  def encoder3[C, A0, A1, A2](f: C => (A0, A1, A2))
                             (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3))
    }

  def encoder4[C, A0, A1, A2, A3]
  (f: C => (A0, A1, A2, A3)) (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2],
                              a3: CellEncoder[A3]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4))
    }

  def encoder5[C, A0, A1, A2, A3, A4]
  (f: C => (A0, A1, A2, A3, A4))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4]):
  RowEncoder[C] = RowEncoder { a =>
    val e = f(a)
    List(a0.encode(e._1),a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5))
  }

  def encoder6[C, A0, A1, A2, A3, A4, A5]
  (f: C => (A0, A1, A2, A3, A4, A5))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6))
    }

  def encoder7[C, A0, A1, A2, A3, A4, A5, A6]
  (f: C => (A0, A1, A2, A3, A4, A5, A6))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7))
    }

  def encoder8[C, A0, A1, A2, A3, A4, A5, A6, A7]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8))
    }

  def encoder9[C, A0, A1, A2, A3, A4, A5, A6, A7, A8]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9))
    }

  def encoder10[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10))
    }

  def encoder11[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11))
    }

  def encoder12[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12))
    }

  def encoder13[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12),
        a12.encode(e._13))
    }

  def encoder14[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12),
        a12.encode(e._13), a13.encode(e._14))
    }

  def encoder15[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14]):
  RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12),
        a12.encode(e._13), a13.encode(e._14), a14.encode(e._15))
    }

  def encoder16[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12),
        a12.encode(e._13), a13.encode(e._14), a14.encode(e._15), a15.encode(e._16))
    }

  def encoder17[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12),
        a12.encode(e._13), a13.encode(e._14), a14.encode(e._15), a15.encode(e._16), a16.encode(e._17))
    }

  def encoder18[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12),
        a12.encode(e._13), a13.encode(e._14), a14.encode(e._15), a15.encode(e._16), a16.encode(e._17),
        a17.encode(e._18))
    }

  def encoder19[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12),
        a12.encode(e._13), a13.encode(e._14), a14.encode(e._15), a15.encode(e._16), a16.encode(e._17),
        a17.encode(e._18), a18.encode(e._19))
    }

  def encoder20[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18], a19: CellEncoder[A19]):
  RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12),
        a12.encode(e._13), a13.encode(e._14), a14.encode(e._15), a15.encode(e._16), a16.encode(e._17),
        a17.encode(e._18), a18.encode(e._19), a19.encode(e._20))
    }

  def encoder21[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18], a19: CellEncoder[A19],
   a20: CellEncoder[A20]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12),
        a12.encode(e._13), a13.encode(e._14), a14.encode(e._15), a15.encode(e._16), a16.encode(e._17),
        a17.encode(e._18), a18.encode(e._19), a19.encode(e._20), a20.encode(e._21))
    }

  def encoder22[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21]
  (f: C => (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21))
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18], a19: CellEncoder[A19],
   a20: CellEncoder[A20], a21: CellEncoder[A21]): RowEncoder[C] =
    RowEncoder { a =>
      val e = f(a)
      List(a0.encode(e._1), a1.encode(e._2), a2.encode(e._3), a3.encode(e._4), a4.encode(e._5), a5.encode(e._6),
        a6.encode(e._7), a7.encode(e._8), a8.encode(e._9), a9.encode(e._10), a10.encode(e._11), a11.encode(e._12),
        a12.encode(e._13), a13.encode(e._14), a14.encode(e._15), a15.encode(e._16), a16.encode(e._17),
        a17.encode(e._18), a18.encode(e._19), a19.encode(e._20), a20.encode(e._21), a21.encode(e._22))
    }


  def caseEncoder1[C, A0: CellEncoder](f: C => Option[A0]): RowEncoder[C] =
    encoder1(f andThen (_.get))

  def caseEncoder2[C, A0, A1](f: C => Option[(A0, A1)])(i0: Int, i1: Int)
                             (implicit a0: CellEncoder[A0], a1: CellEncoder[A1]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](2)

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest.toSeq
    }

  def caseEncoder3[C, A0, A1, A2]
  (f: C => Option[(A0, A1, A2)])
  (i0: Int, i1: Int, i2: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](3)

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest.toSeq
    }

  def caseEncoder4[C, A0, A1, A2, A3]
  (f: C => Option[(A0, A1, A2, A3)])
  (i0: Int, i1: Int, i2: Int, i3: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](4)

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest.toSeq
    }

  def caseEncoder5[C, A0, A1, A2, A3, A4]
  (f: C => Option[(A0, A1, A2, A3, A4)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](5)

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest.toSeq
    }

  def caseEncoder6[C, A0, A1, A2, A3, A4, A5]
  (f: C => Option[(A0, A1, A2, A3, A4, A5)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](6)

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest.toSeq
    }

  def caseEncoder7[C, A0, A1, A2, A3, A4, A5, A6]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](7)

      dest(i0) = a0.encode(e._1)
      dest(i1) = a1.encode(e._2)
      dest(i2) = a2.encode(e._3)
      dest(i3) = a3.encode(e._4)
      dest(i4) = a4.encode(e._5)
      dest(i5) = a5.encode(e._6)
      dest(i6) = a6.encode(e._7)
      dest.toSeq
    }

  def caseEncoder8[C, A0, A1, A2, A3, A4, A5, A6, A7]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](8)

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

  def caseEncoder9[C, A0, A1, A2, A3, A4, A5, A6, A7, A8]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](9)

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

  def caseEncoder10[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](10)

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

  def caseEncoder11[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](11)

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

  def caseEncoder12[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](12)

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

  def caseEncoder13[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](13)

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

  def caseEncoder14[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](14)

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

  def caseEncoder15[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](15)

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

  def caseEncoder16[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](16)

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

  def caseEncoder17[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](17)

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

  def caseEncoder18[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](18)

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

  def caseEncoder19[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](19)

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

  def caseEncoder20[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18], a19: CellEncoder[A19]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](20)

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

  def caseEncoder21[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20]
  (f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int)
  (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
   a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
   a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
   a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18], a19: CellEncoder[A19],
   a20: CellEncoder[A20]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](21)

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

  def caseEncoder22[C, A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
  A21](f: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21)])
      (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
       i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int, i21: Int)
      (implicit a0: CellEncoder[A0], a1: CellEncoder[A1], a2: CellEncoder[A2], a3: CellEncoder[A3], a4: CellEncoder[A4],
       a5: CellEncoder[A5], a6: CellEncoder[A6], a7: CellEncoder[A7], a8: CellEncoder[A8], a9: CellEncoder[A9],
       a10: CellEncoder[A10], a11: CellEncoder[A11], a12: CellEncoder[A12], a13: CellEncoder[A13], a14: CellEncoder[A14],
       a15: CellEncoder[A15], a16: CellEncoder[A16], a17: CellEncoder[A17], a18: CellEncoder[A18], a19: CellEncoder[A19],
       a20: CellEncoder[A20], a21: CellEncoder[A21]): RowEncoder[C] =
    RowEncoder { c =>
      val e = f(c).get
      val dest = new Array[String](22)

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

  implicit def tuple1[A0: CellEncoder]: RowEncoder[Tuple1[A0]] =
    encoder1(_._1)

  implicit def tuple2[A0: CellEncoder, A1: CellEncoder]: RowEncoder[(A0, A1)] =
    encoder2(identity)

  implicit def tuple3[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder]: RowEncoder[(A0, A1, A2)] =
    encoder3(identity)

  implicit def tuple4[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder]: RowEncoder[(A0, A1, A2, A3)] =
    encoder4(identity)

  implicit def tuple5[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder]:
  RowEncoder[(A0, A1, A2, A3, A4)] = encoder5(identity)

  implicit def tuple6[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder]:
  RowEncoder[(A0, A1, A2, A3, A4, A5)] =
    encoder6(identity)

  implicit def tuple7[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6)] =
    encoder7(identity)

  implicit def tuple8[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7)] =
    encoder8(identity)

  implicit def tuple9[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8)] =
    encoder9(identity)

  implicit def tuple10[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8,
    A9)] = encoder10(identity)

  implicit def tuple11[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5,
    A6, A7, A8, A9, A10)] =
    encoder11(identity)

  implicit def tuple12[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder]: RowEncoder[(A0,
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)] =
    encoder12(identity)

  implicit def tuple13[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder]:
  RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)] =
    encoder13(identity)

  implicit def tuple14[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)] =
    encoder14(identity)

  implicit def tuple15[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)] =
    encoder15(identity)

  implicit def tuple16[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12,
    A13, A14, A15)] = encoder16(identity)

  implicit def tuple17[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8,
    A9, A10, A11, A12, A13, A14, A15, A16)] =
    encoder17(identity)

  implicit def tuple18[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4,
    A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)] =
    encoder18(identity)

  implicit def tuple19[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder]: RowEncoder[(A0,
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)] =
    encoder19(identity)

  implicit def tuple20[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder,
  A19: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18,
    A19)] = encoder20(identity)

  implicit def tuple21[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19,
    A20)] = encoder21(identity)

  implicit def tuple22[A0: CellEncoder, A1: CellEncoder, A2: CellEncoder, A3: CellEncoder, A4: CellEncoder, A5: CellEncoder,
  A6: CellEncoder, A7: CellEncoder, A8: CellEncoder, A9: CellEncoder, A10: CellEncoder, A11: CellEncoder, A12: CellEncoder,
  A13: CellEncoder, A14: CellEncoder, A15: CellEncoder, A16: CellEncoder, A17: CellEncoder, A18: CellEncoder, A19: CellEncoder,
  A20: CellEncoder, A21: CellEncoder]: RowEncoder[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21)] =
    encoder22(identity)
}