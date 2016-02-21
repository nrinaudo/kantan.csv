package kantan.csv

import kantan.codecs.laws.CodecValue
import kantan.codecs.laws.CodecValue.{IllegalValue, LegalValue}
import kantan.csv.laws.discipline.RowCodecTests
import kantan.csv.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

// TODO: only CaseClass2 is currently tested. Use the boilerplate plugin to generate the other tests.

/*
object CaseClass1 {
  implicit val arb = Arbitrary(arbitrary[Int].map(CaseClass1.apply))
  implicit val codec = RowCodec.caseCodec1(CaseClass1.apply, CaseClass1.unapply)
}
case class CaseClass1(f1: Int)
*/

object CaseClass2 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int)].map((CaseClass2.apply _).tupled))
  implicit val codec = RowCodec.caseCodec2(CaseClass2.apply, CaseClass2.unapply)(0, 1)
}
case class CaseClass2(f1: Int, f2: Int)

/*
object CaseClass3 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int)].map((CaseClass3.apply _).tupled))
  implicit val codec = RowCodec.caseCodec3(CaseClass3.apply, CaseClass3.unapply)(2, 1, 0)
}
case class CaseClass3(f1: Int, f2: Int, f3: Int)

object CaseClass4 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int)].map((CaseClass4.apply _).tupled))
  implicit val codec = RowCodec.caseCodec4(CaseClass4.apply, CaseClass4.unapply)(3, 2, 1, 0)
}
case class CaseClass4(f1: Int, f2: Int, f3: Int, f4: Int)

object CaseClass5 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int)].map((CaseClass5.apply _).tupled))
  implicit val codec = RowCodec.caseCodec5(CaseClass5.apply, CaseClass5.unapply)(4, 3, 2, 1, 0)
}
case class CaseClass5(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int)

object CaseClass6 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int)].map((CaseClass6.apply _).tupled))
  implicit val codec = RowCodec.caseCodec6(CaseClass6.apply, CaseClass6.unapply)(5, 4, 3, 2, 1, 0)
}
case class CaseClass6(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int)

object CaseClass7 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int)].map((CaseClass7.apply _).tupled))
  implicit val codec = RowCodec.caseCodec7(CaseClass7.apply, CaseClass7.unapply)(6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass7(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int)

object CaseClass8 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass8.apply _).tupled))
  implicit val codec = RowCodec.caseCodec8(CaseClass8.apply, CaseClass8.unapply)(7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass8(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int)


object CaseClass9 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass9.apply _).tupled))
  implicit val codec = RowCodec.caseCodec9(CaseClass9.apply, CaseClass9.unapply)(8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass9(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int)

object CaseClass10 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
    .map((CaseClass10.apply _).tupled))
  implicit val codec = RowCodec.caseCodec10(CaseClass10.apply, CaseClass10.unapply)(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass10(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int)

object CaseClass11 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int)].map((CaseClass11.apply _).tupled))
  implicit val codec = RowCodec.caseCodec11(CaseClass11.apply, CaseClass11.unapply)(10, 9, 8, 7, 6, 5, 4, 3,
    2, 1, 0)
}
case class CaseClass11(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int)

object CaseClass12 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int)].map((CaseClass12.apply _).tupled))
  implicit val codec = RowCodec.caseCodec12(CaseClass12.apply, CaseClass12.unapply)(11, 10, 9, 8, 7, 6, 5, 4, 3,
    2, 1, 0)
}
case class CaseClass12(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int)

object CaseClass13 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int)].map((CaseClass13.apply _).tupled))
  implicit val codec = RowCodec.caseCodec13(CaseClass13.apply, CaseClass13.unapply)(12, 11, 10, 9, 8, 7, 6, 5, 4, 3,
    2, 1, 0)
}
case class CaseClass13(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int)

object CaseClass14 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int)].map((CaseClass14.apply _).tupled))
  implicit val codec = RowCodec.caseCodec14(CaseClass14.apply, CaseClass14.unapply)(13, 12, 11, 10, 9, 8, 7, 6, 5, 4,
    3, 2, 1, 0)
}
case class CaseClass14(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int)

object CaseClass15 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int)].map((CaseClass15.apply _).tupled))
  implicit val codec = RowCodec.caseCodec15(CaseClass15.apply, CaseClass15.unapply)(14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass15(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int)


object CaseClass16 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int)].map((CaseClass16.apply _).tupled))
  implicit val codec = RowCodec.caseCodec16(CaseClass16.apply, CaseClass16.unapply)(15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass16(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int)

object CaseClass17 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int)].map((CaseClass17.apply _).tupled))
  implicit val codec = RowCodec.caseCodec17(CaseClass17.apply, CaseClass17.unapply)(16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass17(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int)

object CaseClass18 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass18.apply _).tupled))
  implicit val codec = RowCodec.caseCodec18(CaseClass18.apply, CaseClass18.unapply)(17, 16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass18(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int, f18: Int)

object CaseClass19 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass19.apply _).tupled))
  implicit val codec = RowCodec.caseCodec19(CaseClass19.apply, CaseClass19.unapply)(18, 17, 16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass19(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int, f18: Int, f19: Int)

object CaseClass20 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass20.apply _).tupled))
  implicit val codec = RowCodec.caseCodec20(CaseClass20.apply, CaseClass20.unapply)(19, 18, 17, 16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass20(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int, f18: Int, f19: Int,
                       f20: Int)

object CaseClass21 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass21.apply _).tupled))
  implicit val codec = RowCodec.caseCodec21(CaseClass21.apply, CaseClass21.unapply)(20, 19, 18, 17, 16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass21(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int, f18: Int, f19: Int,
                       f20: Int, f21: Int)

object CaseClass22 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass22.apply _).tupled))
  implicit val codec = RowCodec.caseCodec22(CaseClass22.apply, CaseClass22.unapply)(21, 20, 19, 18, 17, 16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass22(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int, f18: Int, f19: Int,
                       f20: Int, f21: Int, f22: Int)
*/
class CaseClassTests extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  implicit val arbLegalCaseClass2: Arbitrary[LegalValue[Seq[String], CaseClass2]] =
    Arbitrary(genLegalWith2((i1: Int, i2: Int) ⇒ CaseClass2(i1, i2))((s1: String, s2: String) ⇒ Seq(s1, s2)))

  implicit val arbIllegalCaseClass2: Arbitrary[IllegalValue[Seq[String], CaseClass2]] =
    Arbitrary(genIllegalWith2[String, String, Int, Int, Seq[String], CaseClass2]((s1, s2) ⇒ Seq(s1, s2)))

  //checkAll("CaseClass1", RowCodecTests[CaseClass1].codec[List[String], List[Float]])
  checkAll("CaseClass2", RowCodecTests[CaseClass2].codec[List[String], List[Float]])
  /*
  checkAll("CaseClass3", RowCodecTests[CaseClass3].codec[List[String], List[Float]])
  checkAll("CaseClass4", RowCodecTests[CaseClass4].codec[List[String], List[Float]])
  checkAll("CaseClass5", RowCodecTests[CaseClass5].codec[List[String], List[Float]])
  checkAll("CaseClass6", RowCodecTests[CaseClass6].codec[List[String], List[Float]])
  checkAll("CaseClass7", RowCodecTests[CaseClass7].codec[List[String], List[Float]])
  checkAll("CaseClass8", RowCodecTests[CaseClass8].codec[List[String], List[Float]])
  checkAll("CaseClass9", RowCodecTests[CaseClass9].codec[List[String], List[Float]])
  checkAll("CaseClass10", RowCodecTests[CaseClass10].codec[List[String], List[Float]])
  checkAll("CaseClass11", RowCodecTests[CaseClass11].codec[List[String], List[Float]])
  checkAll("CaseClass12", RowCodecTests[CaseClass12].codec[List[String], List[Float]])
  checkAll("CaseClass13", RowCodecTests[CaseClass13].codec[List[String], List[Float]])
  checkAll("CaseClass14", RowCodecTests[CaseClass14].codec[List[String], List[Float]])
  checkAll("CaseClass15", RowCodecTests[CaseClass15].codec[List[String], List[Float]])
  checkAll("CaseClass16", RowCodecTests[CaseClass16].codec[List[String], List[Float]])
  checkAll("CaseClass17", RowCodecTests[CaseClass17].codec[List[String], List[Float]])
  checkAll("CaseClass18", RowCodecTests[CaseClass18].codec[List[String], List[Float]])
  checkAll("CaseClass19", RowCodecTests[CaseClass19].codec[List[String], List[Float]])
  checkAll("CaseClass20", RowCodecTests[CaseClass20].codec[List[String], List[Float]])
  checkAll("CaseClass21", RowCodecTests[CaseClass21].codec[List[String], List[Float]])
  checkAll("CaseClass22", RowCodecTests[CaseClass22].codec[List[String], List[Float]])
  */
}

