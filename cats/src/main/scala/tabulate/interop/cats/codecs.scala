package tabulate.interop.cats

import export._

//@reexports[DerivedRowEncoder, DerivedRowDecoder, DerivedCellDecoder, DerivedCellEncoder]
@reexports(RowEncoders, RowDecoders, CellEncoders, CellDecoders)
object codecs
