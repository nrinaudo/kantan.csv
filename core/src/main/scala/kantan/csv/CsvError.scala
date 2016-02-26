package kantan.csv

sealed abstract class CsvError extends Product with Serializable

sealed abstract class DecodeError extends CsvError

object DecodeError {
  final case class OutOfBounds(index: Int) extends DecodeError
  final case class TypeError(cause: Throwable) extends DecodeError {
    override def toString: String = s"TypeError(${cause.getMessage})"

    override def equals(obj: Any) = obj match {
      case TypeError(cause2) ⇒ cause.getClass == cause2.getClass
      case _                 ⇒ false
    }
  }
}

sealed abstract class ParseError extends CsvError

object ParseError {
  final case class IOError(cause: Throwable) extends ParseError {
    override def toString: String = s"IOError(${cause.getMessage})"

    override def equals(obj: Any) = obj match {
      case IOError(cause2) ⇒ cause.getClass == cause2.getClass
      case _               ⇒ false
    }
  }
  final case class SyntaxError(line: Int, col: Int) extends ParseError
}