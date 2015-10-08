package com.nrinaudo.csv.cats

import cats.laws.discipline.ArbitraryK
import com.nrinaudo.csv.{RowDecoder, DecodeResult, CellDecoder}
import com.nrinaudo.csv.laws.discipline.arbitrary._
import org.scalacheck.Arbitrary

object arbitrary {
  implicit val arbKCellDecoder: ArbitraryK[CellDecoder] = new ArbitraryK[CellDecoder] {
    override def synthesize[A: Arbitrary]: Arbitrary[CellDecoder[A]] = implicitly
  }

  implicit val arbKRowDecoder: ArbitraryK[RowDecoder] = new ArbitraryK[RowDecoder] {
      override def synthesize[A: Arbitrary]: Arbitrary[RowDecoder[A]] = implicitly
    }

  implicit val arbKDecodeResult: ArbitraryK[DecodeResult] = new ArbitraryK[DecodeResult] {
    override def synthesize[A: Arbitrary]: Arbitrary[DecodeResult[A]] = implicitly
  }
}
