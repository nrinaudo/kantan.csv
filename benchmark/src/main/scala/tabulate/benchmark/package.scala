package tabulate

import tabulate.ops._

package object benchmark {
  type CsvEntry =  (Int, String, Boolean, Float)

  val rawData: List[CsvEntry] = (0x20 to 0x7E).toList.map(i ⇒
    if(i % 2 == 0) (i, s"Character '${i.toChar}' has code point: '$i'", true, i / 100F)
    else           (i, "Character \"" + i.toChar + "\"\nhas code point \r\n" + i, true, i / 100F)
  )

  val strData: String = rawData.asCsv(',')
}
