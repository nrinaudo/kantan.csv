package kantan.csv

sealed abstract class CsvError extends Product with Serializable

object CsvError {
  case class ReadError(line: Int, col: Int) extends CsvError
  case object DecodeError extends CsvError
}