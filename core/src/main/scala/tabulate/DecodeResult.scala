package tabulate

import java.io.IOException

object DecodeResult {
  /** Represents a successful decoding result. */
  case class Success[A](result: A) extends DecodeResult[A] {
    override val isSuccess = true
    override def map[B](f: A => B) = Success(f(result))
    override def flatMap[B](f: A => DecodeResult[B]) = f(result)
    override def getOrElse[B >: A](default: => B) = result
    override def orElse[B >: A](alternative: => DecodeResult[B]) = this
    override def get = result
    override def toOption = Some(result)
  }

  /** Common trait for failure cases. */
  trait Failure extends DecodeResult[Nothing] {
    override def isSuccess = false
    override def map[B](f: Nothing => B) = this
    override def flatMap[B](f: Nothing => DecodeResult[B]) = this
    override def getOrElse[B](default: => B) = default
    override def orElse[B](alternative: => DecodeResult[B]) = alternative
    override def toOption = None
  }

  /** Represents a failure to parse the CSV data (as opposed to one of the cells in a row). */
  case class ReadFailure(line: Int, col: Int) extends Failure {
    override def get = throw new IOException(s"Invalid or corrupt CSV stream line $line column $col.")
  }

  /** Represents a failure to parse a cell in a CSV row. */
  case object DecodeFailure extends Failure {
    override def get = throw new IOException("Invalid data found in CSV row.")
  }

  def success[A](a: A): DecodeResult[A]  = Success(a)
  def readFailure[A](l: Int, c: Int): DecodeResult[A] = ReadFailure(l, c)
  def decodeFailure[A]: DecodeResult[A]  = DecodeFailure
  def apply[A](a: => A): DecodeResult[A] =
    try { success(a) }
    catch { case _: Exception => decodeFailure }
}

trait DecodeResult[+A] {
  def isSuccess: Boolean
  def map[B](f: A => B): DecodeResult[B]
  def flatMap[B](f: A => DecodeResult[B]): DecodeResult[B]
  def orElse[B >: A](alternative: => DecodeResult[B]): DecodeResult[B]
  def getOrElse[B >: A](default: => B): B
  def get: A
  def toOption: Option[A]
}
