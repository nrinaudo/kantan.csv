package kantan.csv

import kantan.codecs.Result

object ParseResult {
  def apply[A](a: ⇒ A): ParseResult[A] = Result.nonFatal(a).leftMap(ParseError.IOError.apply)
  def syntax(line: Int, col: Int): ParseResult[Nothing] = Result.failure(ParseError.SyntaxError(line, col))
  def noSuchElement: ParseResult[Nothing] = Result.failure(ParseError.NoSuchElement)
  def success[A](a: A): ParseResult[A] = Result.success(a)
}
