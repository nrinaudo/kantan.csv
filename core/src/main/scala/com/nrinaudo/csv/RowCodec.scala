package com.nrinaudo.csv

trait RowCodec[A] extends RowDecoder[A] with RowEncoder[A]

object RowCodec {
  implicit def apply[C](implicit r: RowDecoder[C], w: RowEncoder[C]): RowCodec[C] = apply(r.decode _ , w.encode _)

  def apply[C](decoder: Seq[String] => DecodeResult[C], encoder: C => Seq[String]): RowCodec[C] = new RowCodec[C] {
    override def encode(a: C) = encoder(a)
    override def decode(row: Seq[String]) = decoder(row)
  }

  def caseCodec1[C, A0: CellCodec](f: A0 => C, g: C => Option[A0]): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder1(f), RowEncoder.caseEncoder1(g))

  def caseCodec2[C, A0: CellCodec, A1: CellCodec](f: (A0, A1) => C, g: C => Option[(A0, A1)])
                                                    (i0: Int, i1: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder2(f)(i0, i1), RowEncoder.caseEncoder2(g)(i0, i1))

  def caseCodec3[C, A0: CellCodec, A1: CellCodec, A2: CellCodec](f: (A0, A1, A2) => C, g: C => Option[(A0, A1, A2)])
                                                                    (i0: Int, i1: Int, i2: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder3(f)(i0, i1, i2), RowEncoder.caseEncoder3(g)(i0, i1, i2))

  def caseCodec4[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec]
  (f: (A0, A1, A2, A3) => C, g: C => Option[(A0, A1, A2, A3)])
  (i0: Int, i1: Int, i2: Int, i3: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder4(f)(i0, i1, i2, i3), RowEncoder.caseEncoder4(g)(i0, i1, i2, i3))

  def caseCodec5[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec]
  (f: (A0, A1, A2, A3, A4) => C, g: C => Option[(A0, A1, A2, A3, A4)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder5(f)(i0, i1, i2, i3, i4), RowEncoder.caseEncoder5(g)(i0, i1, i2, i3, i4))

  def caseCodec6[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5) => C, g: C => Option[(A0, A1, A2, A3, A4, A5)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder6(f)(i0, i1, i2, i3, i4, i5),
      RowEncoder.caseEncoder6(g)(i0, i1, i2, i3, i4, i5))

  def caseCodec7[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec](f: (A0, A1, A2, A3, A4, A5, A6) => C, g: C => Option[(A0, A1, A2, A3, A4, A5, A6)])
                 (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder7(f)(i0, i1, i2, i3, i4, i5, i6),
      RowEncoder.caseEncoder7(g)(i0, i1, i2, i3, i4, i5, i6))

  def caseCodec8[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7) => C, g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder8(f)(i0, i1, i2, i3, i4, i5, i6, i7),
      RowEncoder.caseEncoder8(g)(i0, i1, i2, i3, i4, i5, i6, i7))

  def caseCodec9[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8) => C, g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder9(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8),
      RowEncoder.caseEncoder9(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8))

  def caseCodec10[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder10(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9),
      RowEncoder.caseEncoder10(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9))

  def caseCodec11[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder11(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10),
      RowEncoder.caseEncoder11(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10))

  def caseCodec12[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int):
  RowCodec[C] = RowCodec(RowDecoder.caseDecoder12(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11),
    RowEncoder.caseEncoder12(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11))

  def caseCodec13[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder13(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12),
      RowEncoder.caseEncoder13(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12))

  def caseCodec14[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder14(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13),
      RowEncoder.caseEncoder14(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13))

  def caseCodec15[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder15(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14),
      RowEncoder.caseEncoder15(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14))

  def caseCodec16[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder16(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15),
      RowEncoder.caseEncoder16(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15))

  def caseCodec17[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder17(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16),
      RowEncoder.caseEncoder17(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16))

  def caseCodec18[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec, A17: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder18(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17),
      RowEncoder.caseEncoder18(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17))

  def caseCodec19[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec, A17: CellCodec, A18: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder19(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17,
      i18), RowEncoder.caseEncoder19(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14,
      i15, i16, i17, i18))

  def caseCodec20[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec, A17: CellCodec, A18: CellCodec, A19: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder20(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17,
      i18, i19), RowEncoder.caseEncoder20(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14,
      i15, i16, i17, i18, i19))

  def caseCodec21[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec, A17: CellCodec, A18: CellCodec, A19: CellCodec,
  A20: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder21(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17,
      i18, i19, i20), RowEncoder.caseEncoder21(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14,
      i15, i16, i17, i18, i19, i20))

  def caseCodec22[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec, A17: CellCodec, A18: CellCodec, A19: CellCodec,
  A20: CellCodec, A21: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int, i21: Int): RowCodec[C] =
    RowCodec(RowDecoder.caseDecoder22(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17,
      i18, i19, i20, i21), RowEncoder.caseEncoder22(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14,
      i15, i16, i17, i18, i19, i20, i21))
}
