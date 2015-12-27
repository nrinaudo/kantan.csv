package tabulate

import java.io.Reader

/** Turns raw CSV data into [[CsvRows]] instances.
  *
  * A default, internal implementation is available implicitly. It can be overridden simply by bringing an implicit
  * instance in scope. This can be useful for applications that require a specific CSV parsing library, such as
  * [[https://github.com/FasterXML/jackson-dataformat-csv Jackson CSV]].
  */
trait CsvParser {
  def parse(reader: Reader, separator: Char): CsvRows[DecodeResult[Seq[String]]]
}

object CsvParser {
  def apply(f: (Reader, Char) => CsvRows[DecodeResult[Seq[String]]]): CsvParser = new CsvParser {
    override def parse(reader: Reader, separator: Char): CsvRows[DecodeResult[Seq[String]]] = f(reader, separator)
  }

  /** Default, internal implementation.
    *
    * This should be fast and robust enough for all use cases, but can be overridden should the need arise.
    */
  implicit val internal: CsvParser = CsvParser((reader, sep) => new InternalParser(reader, sep))
}