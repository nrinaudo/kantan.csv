package com.nrinaudo.csv

trait RowFormat[A] extends RowDecoder[A] with RowEncoder[A]

object RowFormat {
  implicit def apply[C](implicit r: RowDecoder[C], w: RowEncoder[C]): RowFormat[C] = apply(r.decode _ , w.encode _)

  def apply[C](reader: Seq[String] => Option[C], writer: C => Seq[String]): RowFormat[C] = new RowFormat[C] {
    override def encode(a: C) = writer(a)
    override def decode(row: Seq[String]) = reader(row)
  }

  def caseFormat1[C, A0: CellCodec](f: A0 => C, g: C => Option[A0]): RowFormat[C] =
    RowFormat(RowDecoder.caseReader1(f), RowEncoder.caseWriter1(g))

  def caseFormat2[C, A0: CellCodec, A1: CellCodec](f: (A0, A1) => C, g: C => Option[(A0, A1)])
                                                    (i0: Int, i1: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader2(f)(i0, i1), RowEncoder.caseWriter2(g)(i0, i1))

  def caseFormat3[C, A0: CellCodec, A1: CellCodec, A2: CellCodec](f: (A0, A1, A2) => C, g: C => Option[(A0, A1, A2)])
                                                                    (i0: Int, i1: Int, i2: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader3(f)(i0, i1, i2), RowEncoder.caseWriter3(g)(i0, i1, i2))

  def caseFormat4[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec]
  (f: (A0, A1, A2, A3) => C, g: C => Option[(A0, A1, A2, A3)])
  (i0: Int, i1: Int, i2: Int, i3: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader4(f)(i0, i1, i2, i3), RowEncoder.caseWriter4(g)(i0, i1, i2, i3))

  def caseFormat5[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec]
  (f: (A0, A1, A2, A3, A4) => C, g: C => Option[(A0, A1, A2, A3, A4)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader5(f)(i0, i1, i2, i3, i4), RowEncoder.caseWriter5(g)(i0, i1, i2, i3, i4))

  def caseFormat6[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5) => C, g: C => Option[(A0, A1, A2, A3, A4, A5)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader6(f)(i0, i1, i2, i3, i4, i5),
      RowEncoder.caseWriter6(g)(i0, i1, i2, i3, i4, i5))

  def caseFormat7[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec](f: (A0, A1, A2, A3, A4, A5, A6) => C, g: C => Option[(A0, A1, A2, A3, A4, A5, A6)])
                 (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader7(f)(i0, i1, i2, i3, i4, i5, i6),
      RowEncoder.caseWriter7(g)(i0, i1, i2, i3, i4, i5, i6))

  def caseFormat8[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7) => C, g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader8(f)(i0, i1, i2, i3, i4, i5, i6, i7),
      RowEncoder.caseWriter8(g)(i0, i1, i2, i3, i4, i5, i6, i7))

  def caseFormat9[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8) => C, g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader9(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8),
      RowEncoder.caseWriter9(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8))

  def caseFormat10[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader10(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9),
      RowEncoder.caseWriter10(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9))

  def caseFormat11[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader11(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10),
      RowEncoder.caseWriter11(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10))

  def caseFormat12[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int):
  RowFormat[C] = RowFormat(RowDecoder.caseReader12(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11),
    RowEncoder.caseWriter12(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11))

  def caseFormat13[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader13(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12),
      RowEncoder.caseWriter13(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12))

  def caseFormat14[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader14(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13),
      RowEncoder.caseWriter14(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13))

  def caseFormat15[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader15(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14),
      RowEncoder.caseWriter15(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14))

  def caseFormat16[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader16(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15),
      RowEncoder.caseWriter16(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15))

  def caseFormat17[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader17(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16),
      RowEncoder.caseWriter17(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16))

  def caseFormat18[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec, A17: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader18(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17),
      RowEncoder.caseWriter18(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17))

  def caseFormat19[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec, A17: CellCodec, A18: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader19(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17,
      i18), RowEncoder.caseWriter19(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14,
      i15, i16, i17, i18))

  def caseFormat20[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec, A17: CellCodec, A18: CellCodec, A19: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader20(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17,
      i18, i19), RowEncoder.caseWriter20(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14,
      i15, i16, i17, i18, i19))

  def caseFormat21[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec, A17: CellCodec, A18: CellCodec, A19: CellCodec,
  A20: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader21(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17,
      i18, i19, i20), RowEncoder.caseWriter21(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14,
      i15, i16, i17, i18, i19, i20))

  def caseFormat22[C, A0: CellCodec, A1: CellCodec, A2: CellCodec, A3: CellCodec, A4: CellCodec, A5: CellCodec,
  A6: CellCodec, A7: CellCodec, A8: CellCodec, A9: CellCodec, A10: CellCodec, A11: CellCodec, A12: CellCodec,
  A13: CellCodec, A14: CellCodec, A15: CellCodec, A16: CellCodec, A17: CellCodec, A18: CellCodec, A19: CellCodec,
  A20: CellCodec, A21: CellCodec]
  (f: (A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21) => C,
   g: C => Option[(A0, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21)])
  (i0: Int, i1: Int, i2: Int, i3: Int, i4: Int, i5: Int, i6: Int, i7: Int, i8: Int, i9: Int, i10: Int, i11: Int,
   i12: Int, i13: Int, i14: Int, i15: Int, i16: Int, i17: Int, i18: Int, i19: Int, i20: Int, i21: Int): RowFormat[C] =
    RowFormat(RowDecoder.caseReader22(f)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14, i15, i16, i17,
      i18, i19, i20, i21), RowEncoder.caseWriter22(g)(i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i10, i11, i12, i13, i14,
      i15, i16, i17, i18, i19, i20, i21))
}
