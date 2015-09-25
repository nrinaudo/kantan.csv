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
  def withHeader: RowReader[A] = RowReader(true, read _)
  def noHeader: RowReader[A] = RowReader(false, read _)
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

  def caseReader2[A1: CellReader, A2: CellReader, R](f: (A1, A2) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1))
  }

  def caseReader3[A1: CellReader, A2: CellReader, A3: CellReader, R](f: (A1, A2, A3) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2))
  }

  def caseReader4[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, R]
  (f: (A1, A2, A3, A4) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3))
  }

  def caseReader5[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, R]
  (f: (A1, A2, A3, A4, A5) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4))
  }

  def caseReader6[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5))
  }

  def caseReader7[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6))
  }

  def caseReader8[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, R](f: (A1, A2, A3, A4, A5, A6, A7, A8) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7))
  }

  def caseReader9[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, R](f: (A1, A2, A3, A4, A5, A6, A7, A8, A9) => R): RowReader[R] =
    apply { ss =>
      f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
        r[A9](ss, 8))
    }

  def caseReader10[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
      r[A9](ss, 8), r[A10](ss, 9))
  }

  def caseReader11[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
      r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10))
  }

  def caseReader12[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
      r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11))
  }

  def caseReader13[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
      r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11), r[A13](ss, 12))
  }

  def caseReader14[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
      r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11), r[A13](ss, 12), r[A14](ss, 13))
  }

  def caseReader15[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
      r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11), r[A13](ss, 12), r[A14](ss, 13), r[A15](ss, 14))
  }

  def caseReader16[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
      r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11), r[A13](ss, 12), r[A14](ss, 13), r[A15](ss, 14),
      r[A16](ss, 15))
  }

  def caseReader17[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17) => R): RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
      r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11), r[A13](ss, 12), r[A14](ss, 13), r[A15](ss, 14),
      r[A16](ss, 15), r[A17](ss, 16))
  }

  def caseReader18[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18) => R): RowReader[R] =
    apply { ss =>
      f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
        r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11), r[A13](ss, 12), r[A14](ss, 13), r[A15](ss, 14),
        r[A16](ss, 15), r[A17](ss, 16), r[A18](ss, 17))
    }

  def caseReader19[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19) => R): RowReader[R] =
    apply { ss =>
      f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
        r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11), r[A13](ss, 12), r[A14](ss, 13), r[A15](ss, 14),
        r[A16](ss, 15), r[A17](ss, 16), r[A18](ss, 17), r[A19](ss, 18))
    }

  def caseReader20[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader,
  A20: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20) => R): RowReader[R] =
    apply { ss =>
      f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
        r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11), r[A13](ss, 12), r[A14](ss, 13), r[A15](ss, 14),
        r[A16](ss, 15), r[A17](ss, 16), r[A18](ss, 17), r[A19](ss, 18), r[A20](ss, 19))
    }

  def caseReader21[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader,
  A20: CellReader, A21: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21) => R):
  RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
      r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11), r[A13](ss, 12), r[A14](ss, 13), r[A15](ss, 14),
      r[A16](ss, 15), r[A17](ss, 16), r[A18](ss, 17), r[A19](ss, 18), r[A20](ss, 19), r[A21](ss, 20))
  }

  def caseReader22[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader,
  A20: CellReader, A21: CellReader, A22: CellReader, R]
  (f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22) => R):
  RowReader[R] = apply { ss =>
    f(r[A1](ss, 0), r[A2](ss, 1), r[A3](ss, 2), r[A4](ss, 3), r[A5](ss, 4), r[A6](ss, 5), r[A7](ss, 6), r[A8](ss, 7),
      r[A9](ss, 8), r[A10](ss, 9), r[A11](ss, 10), r[A12](ss, 11), r[A13](ss, 12), r[A14](ss, 13), r[A15](ss, 14),
      r[A16](ss, 15), r[A17](ss, 16), r[A18](ss, 17), r[A19](ss, 18), r[A20](ss, 19), r[A21](ss, 20), r[A22](ss, 21))
  }


  // - Tuple readers ---------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit def tuple2[A1: CellReader, A2: CellReader]: RowReader[(A1, A2)] =
    caseReader2(Tuple2.apply[A1, A2])

  implicit def tuple3[A1: CellReader, A2: CellReader, A3: CellReader]: RowReader[(A1, A2, A3)] =
    caseReader3(Tuple3.apply[A1, A2, A3])

  implicit def tuple4[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader]: RowReader[(A1, A2, A3, A4)] =
    caseReader4(Tuple4.apply[A1, A2, A3, A4])

  implicit def tuple5[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader]:
  RowReader[(A1, A2, A3, A4, A5)] = caseReader5(Tuple5.apply[A1, A2, A3, A4, A5])

  implicit def tuple6[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6)] = caseReader6(Tuple6.apply[A1, A2, A3, A4, A5, A6])

  implicit def tuple7[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7)] =
    caseReader7(Tuple7.apply[A1, A2, A3, A4, A5, A6, A7])

  implicit def tuple8[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8)] =
    caseReader8(Tuple8.apply[A1, A2, A3, A4, A5, A6, A7, A8])

  implicit def tuple9[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9)] =
    caseReader9(Tuple9.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9])

  implicit def tuple10[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)] =
    caseReader10(Tuple10.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10])

  implicit def tuple11[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)] =
    caseReader11(Tuple11.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11])

  implicit def tuple12[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)] =
    caseReader12(Tuple12.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12])

  implicit def tuple13[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)] =
    caseReader13(Tuple13.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13])

  implicit def tuple14[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)] =
    caseReader14(Tuple14.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14])

  implicit def tuple15[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)] =
    caseReader15(Tuple15.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15])

  implicit def tuple16[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)] =
    caseReader16(Tuple16.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16])

  implicit def tuple17[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)] =
    caseReader17(Tuple17.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17])

  implicit def tuple18[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)] =
    caseReader18(Tuple18.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18])

  implicit def tuple19[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19)] =
    caseReader19(Tuple19.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19])

  implicit def tuple20[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader, A20: CellReader]:
  RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20)] =
    caseReader20(Tuple20.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20])

  implicit def tuple21[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader,
  A20: CellReader, A21: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16,
    A17, A18, A19, A20, A21)] = caseReader21(Tuple21.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14,
    A15, A16, A17, A18, A19, A20, A21])

  implicit def tuple22[A1: CellReader, A2: CellReader, A3: CellReader, A4: CellReader, A5: CellReader, A6: CellReader,
  A7: CellReader, A8: CellReader, A9: CellReader, A10: CellReader, A11: CellReader, A12: CellReader, A13: CellReader,
  A14: CellReader, A15: CellReader, A16: CellReader, A17: CellReader, A18: CellReader, A19: CellReader,
  A20: CellReader, A21: CellReader, A22: CellReader]: RowReader[(A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12,
    A13, A14, A15, A16, A17, A18, A19, A20, A21, A22)] = caseReader22(Tuple22.apply[A1, A2, A3, A4, A5, A6, A7, A8, A9,
    A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22])
}