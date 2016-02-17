package kantan.csv

import kantan.codecs.Result

object CsvResult {
  def apply[A](a: ⇒ A): CsvResult[A] = Result.nonFatalOr(CsvError.DecodeError)(a)
}
