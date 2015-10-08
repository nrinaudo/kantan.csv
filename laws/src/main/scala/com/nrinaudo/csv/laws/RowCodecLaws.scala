package com.nrinaudo.csv.laws

import com.nrinaudo.csv.{RowCodec, DecodeResult}
import com.nrinaudo.csv.ops._

trait RowCodecLaws[A] {
  implicit def codec: RowCodec[A]

    def encodeReversibility(a: A): Boolean = codec.decode(a.asCsvRow) == DecodeResult.Success(a)

    def decodeIdentity(a: A): Boolean = codec.decode(a.asCsvRow) == codec.map(identity).decode(a.asCsvRow)

    def decodeComposition[B, C](a: A, f: A => B, g: B => C): Boolean =
      codec.map(f andThen g).decode(a.asCsvRow) == codec.map(f).map(g).decode(a.asCsvRow)

    def encodeIdentity(a: A): Boolean = codec.encode(a) == codec.contramap[A](identity).encode(a)

    def encodeComposition[B, C](c: C, f: B => A, g: C => B): Boolean =
      codec.contramap(g andThen f).encode(c) == codec.contramap(f).contramap(g).encode(c)
}

object RowCodecLaws {
  def apply[A](implicit c: RowCodec[A]): RowCodecLaws[A] = new RowCodecLaws[A] {
    override implicit def codec = c
  }
}