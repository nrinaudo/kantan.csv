package kantan.csv

import kantan.codecs.Result

object DecodeResult {
  def apply[A](a: ⇒ A): DecodeResult[A] = Result.nonFatal(a).leftMap(DecodeError.TypeError.apply)
  def outOfBounds(index: Int): DecodeResult[Nothing] = Result.failure(DecodeError.OutOfBounds(index))
  def success[A](a: A): DecodeResult[A] = Result.success(a)
}
