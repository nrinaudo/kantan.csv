package com.nrinaudo.csv

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import RowCodecTest._
import ops._

object RowCodecTest {
  implicit def tuple1[A: Arbitrary]: Arbitrary[Tuple1[A]] = Arbitrary(arbitrary[A].map(Tuple1.apply))
}

abstract class RowCodecTest[R: RowCodec: Arbitrary] extends FunSuite with GeneratorDrivenPropertyChecks {
  test("decode(encode(r)) must be equal to r for any r") {
    forAll { r: R =>
      assert(RowDecoder[R].decode(r.asCsvRow) == DecodeResult.Success(r))
    }
  }

  test("The covariant functor composition law must be respected") {
    forAll { (r: R, f: R => Int) =>
      assert(RowDecoder[R].decode(r.asCsvRow).map(f) == RowDecoder[R].map(f).decode(r.asCsvRow))
    }
  }

  test("The covariant functor identity law must be respected") {
    forAll { r: R =>
      assert(RowDecoder[R].decode(r.asCsvRow) == RowDecoder[R].map(identity).decode(r.asCsvRow))
    }
  }

  test("The contravariant functor composition law must be respected") {
    forAll { (i: Int, f: Int => R) =>
      assert(RowEncoder[R].encode(f(i)) == RowEncoder[R].contramap[Int](f).encode(i))
    }
  }

  test("The contravariant functor identity law must be respected") {
    forAll { r: R =>
      assert(RowEncoder[R].encode(r) == RowEncoder[R].contramap[R](identity).encode(r))
    }
  }
}


// - Collection tests --------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
class ListRowCodecTest extends RowCodecTest[List[Int]]
class VectorRowCodecTest extends RowCodecTest[Vector[Int]]
class SeqRowCodecTest extends RowCodecTest[Seq[Int]]
class StreamRowCodecTest extends RowCodecTest[Stream[Int]]
class EitherRowCodecTest extends RowCodecTest[Either[List[Int], (Boolean, String)]]


// - Tuple tests -------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
class Tuple1CodecTest extends RowCodecTest[Tuple1[Int]]
class Tuple2CodecTest extends RowCodecTest[(Int, Int)]
class Tuple3CodecTest extends RowCodecTest[(Int, Int, Int)]
class Tuple4CodecTest extends RowCodecTest[(Int, Int, Int, Int)]
class Tuple5CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int)]
class Tuple6CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int)]
class Tuple7CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int)]
class Tuple8CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple9CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple10CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple11CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple12CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple13CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple14CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
class Tuple15CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int)]
class Tuple16CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int)]
class Tuple17CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int)]
class Tuple18CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int, Int)]
class Tuple19CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int, Int, Int)]
class Tuple20CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int, Int, Int, Int)]
class Tuple21CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int, Int, Int, Int, Int)]
class Tuple22CodecTest extends RowCodecTest[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
  Int, Int, Int, Int, Int, Int, Int, Int)]



// - Case class tests -------------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
object CaseClass1 {
  implicit val arb = Arbitrary(arbitrary[Int].map(CaseClass1.apply))
  implicit val codec = RowCodec.caseCodec1(CaseClass1.apply, CaseClass1.unapply)
}
case class CaseClass1(f1: Int)
class CaseClass1Test extends RowCodecTest[CaseClass1]

object CaseClass2 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int)].map((CaseClass2.apply _).tupled))
  implicit val codec = RowCodec.caseCodec2(CaseClass2.apply, CaseClass2.unapply)(1, 0)
}
case class CaseClass2(f1: Int, f2: Int)
class CaseClass2Test extends RowCodecTest[CaseClass2]

object CaseClass3 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int)].map((CaseClass3.apply _).tupled))
  implicit val codec = RowCodec.caseCodec3(CaseClass3.apply, CaseClass3.unapply)(2, 1, 0)
}
case class CaseClass3(f1: Int, f2: Int, f3: Int)
class CaseClass3Test extends RowCodecTest[CaseClass3]

object CaseClass4 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int)].map((CaseClass4.apply _).tupled))
  implicit val codec = RowCodec.caseCodec4(CaseClass4.apply, CaseClass4.unapply)(3, 2, 1, 0)
}
case class CaseClass4(f1: Int, f2: Int, f3: Int, f4: Int)
class CaseClass4Test extends RowCodecTest[CaseClass4]

object CaseClass5 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int)].map((CaseClass5.apply _).tupled))
  implicit val codec = RowCodec.caseCodec5(CaseClass5.apply, CaseClass5.unapply)(4, 3, 2, 1, 0)
}
case class CaseClass5(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int)
class CaseClass5Test extends RowCodecTest[CaseClass5]

object CaseClass6 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int)].map((CaseClass6.apply _).tupled))
  implicit val codec = RowCodec.caseCodec6(CaseClass6.apply, CaseClass6.unapply)(5, 4, 3, 2, 1, 0)
}
case class CaseClass6(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int)
class CaseClass6Test extends RowCodecTest[CaseClass6]

object CaseClass7 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int)].map((CaseClass7.apply _).tupled))
  implicit val codec = RowCodec.caseCodec7(CaseClass7.apply, CaseClass7.unapply)(6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass7(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int)
class CaseClass7Test extends RowCodecTest[CaseClass7]

object CaseClass8 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass8.apply _).tupled))
  implicit val codec = RowCodec.caseCodec8(CaseClass8.apply, CaseClass8.unapply)(7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass8(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int)
class CaseClass8Test extends RowCodecTest[CaseClass8]

object CaseClass9 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass9.apply _).tupled))
  implicit val codec = RowCodec.caseCodec9(CaseClass9.apply, CaseClass9.unapply)(8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass9(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int)
class CaseClass9Test extends RowCodecTest[CaseClass9]

object CaseClass10 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)]
    .map((CaseClass10.apply _).tupled))
  implicit val codec = RowCodec.caseCodec10(CaseClass10.apply, CaseClass10.unapply)(9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass10(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int)
class CaseClass10Test extends RowCodecTest[CaseClass10]

object CaseClass11 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int)].map((CaseClass11.apply _).tupled))
  implicit val codec = RowCodec.caseCodec11(CaseClass11.apply, CaseClass11.unapply)(10, 9, 8, 7, 6, 5, 4, 3,
    2, 1, 0)
}
case class CaseClass11(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int)
class CaseClass11Test extends RowCodecTest[CaseClass11]

object CaseClass12 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int)].map((CaseClass12.apply _).tupled))
  implicit val codec = RowCodec.caseCodec12(CaseClass12.apply, CaseClass12.unapply)(11, 10, 9, 8, 7, 6, 5, 4, 3,
    2, 1, 0)
}
case class CaseClass12(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int)
class CaseClass12Test extends RowCodecTest[CaseClass12]

object CaseClass13 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int)].map((CaseClass13.apply _).tupled))
  implicit val codec = RowCodec.caseCodec13(CaseClass13.apply, CaseClass13.unapply)(12, 11, 10, 9, 8, 7, 6, 5, 4, 3,
    2, 1, 0)
}
case class CaseClass13(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int)
class CaseClass13Test extends RowCodecTest[CaseClass13]

object CaseClass14 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int)].map((CaseClass14.apply _).tupled))
  implicit val codec = RowCodec.caseCodec14(CaseClass14.apply, CaseClass14.unapply)(13, 12, 11, 10, 9, 8, 7, 6, 5, 4,
    3, 2, 1, 0)
}
case class CaseClass14(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int)
class CaseClass14Test extends RowCodecTest[CaseClass14]

object CaseClass15 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int)].map((CaseClass15.apply _).tupled))
  implicit val codec = RowCodec.caseCodec15(CaseClass15.apply, CaseClass15.unapply)(14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass15(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int)
class CaseClass15Test extends RowCodecTest[CaseClass15]

object CaseClass16 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int)].map((CaseClass16.apply _).tupled))
  implicit val codec = RowCodec.caseCodec16(CaseClass16.apply, CaseClass16.unapply)(15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass16(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int)
class CaseClass16Test extends RowCodecTest[CaseClass16]

object CaseClass17 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int)].map((CaseClass17.apply _).tupled))
  implicit val codec = RowCodec.caseCodec17(CaseClass17.apply, CaseClass17.unapply)(16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass17(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int)
class CaseClass17Test extends RowCodecTest[CaseClass17]

object CaseClass18 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass18.apply _).tupled))
  implicit val codec = RowCodec.caseCodec18(CaseClass18.apply, CaseClass18.unapply)(17, 16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass18(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int, f18: Int)
class CaseClass18Test extends RowCodecTest[CaseClass18]

object CaseClass19 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass19.apply _).tupled))
  implicit val codec = RowCodec.caseCodec19(CaseClass19.apply, CaseClass19.unapply)(18, 17, 16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass19(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int, f18: Int, f19: Int)
class CaseClass19Test extends RowCodecTest[CaseClass19]

object CaseClass20 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass20.apply _).tupled))
  implicit val codec = RowCodec.caseCodec20(CaseClass20.apply, CaseClass20.unapply)(19, 18, 17, 16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass20(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int, f18: Int, f19: Int,
                       f20: Int)
class CaseClass20Test extends RowCodecTest[CaseClass20]

object CaseClass21 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass21.apply _).tupled))
  implicit val codec = RowCodec.caseCodec21(CaseClass21.apply, CaseClass21.unapply)(20, 19, 18, 17, 16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass21(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int, f18: Int, f19: Int,
                       f20: Int, f21: Int)
class CaseClass21Test extends RowCodecTest[CaseClass21]

object CaseClass22 {
  implicit val arb = Arbitrary(arbitrary[(Int, Int, Int, Int, Int, Int, Int, Int, Int, Int,
    Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)].map((CaseClass22.apply _).tupled))
  implicit val codec = RowCodec.caseCodec22(CaseClass22.apply, CaseClass22.unapply)(21, 20, 19, 18, 17, 16, 15, 14,
    13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0)
}
case class CaseClass22(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int, f7: Int, f8: Int, f9: Int, f10: Int,
                       f11: Int, f12: Int, f13: Int, f14: Int, f15: Int, f16: Int, f17: Int, f18: Int, f19: Int,
                       f20: Int, f21: Int, f22: Int)
class CaseClass22Test extends RowCodecTest[CaseClass22]