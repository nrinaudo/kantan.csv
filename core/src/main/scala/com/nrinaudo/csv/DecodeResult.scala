package com.nrinaudo.csv

import java.io.IOException

object DecodeResult {
  case class Success[A](result: A) extends DecodeResult[A] {
    override val isSuccess = true
    override def map[B](f: A => B) = Success(f(result))
    override def flatMap[B](f: A => DecodeResult[B]) = f(result)
    override def getOrElse[B >: A](default: => B) = result
    override def orElse[B >: A](alternative: => DecodeResult[B]) = this
    override def get = result
  }

  trait Failure extends DecodeResult[Nothing] {
    override def isSuccess = false
    override def map[B](f: Nothing => B) = this
    override def flatMap[B](f: Nothing => DecodeResult[B]) = this
    override def getOrElse[B](default: => B) = default
    override def orElse[B](alternative: => DecodeResult[B]) = alternative
  }

  case object ReadFailure extends Failure {
    override def get = throw new IOException("Invalid or corrupt CSV stream.")
  }

  case object DecodeFailure extends Failure {
    override def get = throw new IOException("Invalid data found in CSV row.")
  }

  def success[A](a: A): DecodeResult[A]  = Success(a)
  def readFailure[A]: DecodeResult[A]    = ReadFailure
  def decodeFailure[A]: DecodeResult[A]  = DecodeFailure
  def apply[A](a: => A): DecodeResult[A] =
    try { success(a) }
    catch { case _: Exception => readFailure }
}

trait DecodeResult[+A] {
  def isSuccess: Boolean
  def map[B](f: A => B): DecodeResult[B]
  def flatMap[B](f: A => DecodeResult[B]): DecodeResult[B]
  def orElse[B >: A](alternative: => DecodeResult[B]): DecodeResult[B]
  def getOrElse[B >: A](default: => B): B
  def get: A
}
