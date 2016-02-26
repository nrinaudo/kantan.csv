package kantan

import kantan.codecs.Result

package object csv {
  type CsvResult[A] = Result[CsvError, A]
  type ParseResult[A] = Result[ParseError, A]
  type DecodeResult[A] = Result[DecodeError, A]
}
