package kantan.csv

import kantan.codecs.Result

object CsvResult {
  def success[A](a: A): CsvResult[A] = Result.success(a)
  def apply[A](a: ⇒ A): CsvResult[A] = Result.nonFatalOr(CsvError.DecodeError)(a)
  val decodeError: CsvResult[Nothing] = Result.failure(CsvError.DecodeError)
  def readError(line: Int, col: Int): CsvResult[Nothing] = Result.failure(CsvError.ReadError(line, col))
}
