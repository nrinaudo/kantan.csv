package kantan

import kantan.codecs.Result

package object csv {
  type CsvResult[A] = Result[CsvError, A]
}
