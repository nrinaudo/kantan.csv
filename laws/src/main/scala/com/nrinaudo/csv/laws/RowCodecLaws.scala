package com.nrinaudo.csv.laws

import java.io.StringWriter

import com.nrinaudo.csv.{CsvInput, RowCodec, DecodeResult}
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

  def csvReversibility(as: List[A]): Boolean = {
    // Writes as as CSV
    val sw = new StringWriter()
    as.foldLeft(sw.asCsvWriter[A](','))(_ write _).close()

    // Parses it, makes sure it didn't get "corrupt"
    as == CsvInput[String].unsafeRows[A](sw.toString, ',', false).toList
  }
}

object RowCodecLaws {
  def apply[A](implicit c: RowCodec[A]): RowCodecLaws[A] = new RowCodecLaws[A] {
    override implicit def codec = c
  }
}