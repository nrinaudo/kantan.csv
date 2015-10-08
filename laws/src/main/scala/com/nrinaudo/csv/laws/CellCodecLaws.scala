package com.nrinaudo.csv.laws

import com.nrinaudo.csv.{RowCodec, CellCodec, DecodeResult}
import com.nrinaudo.csv.ops._

trait CellCodecLaws[A] {
  implicit def codec: CellCodec[A]

  def encodeReversibility(a: A): Boolean = codec.decode(a.asCsvCell) == DecodeResult.Success(a)

  def decodeIdentity(a: A): Boolean = codec.decode(a.asCsvCell) == codec.map(identity).decode(a.asCsvCell)

  def decodeComposition[B, C](a: A, f: A => B, g: B => C): Boolean =
    codec.map(f andThen g).decode(a.asCsvCell) == codec.map(f).map(g).decode(a.asCsvCell)

  def encodeIdentity(a: A): Boolean = codec.encode(a) == codec.contramap[A](identity).encode(a)

  def encodeComposition[B, C](c: C, f: B => A, g: C => B): Boolean =
    codec.contramap(g andThen f).encode(c) == codec.contramap(f).contramap(g).encode(c)
}

object CellCodecLaws {
  def apply[A](implicit c: CellCodec[A]): CellCodecLaws[A] = new CellCodecLaws[A] {
    override implicit def codec = c
  }
}