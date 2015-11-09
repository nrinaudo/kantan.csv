package tabulate.benchmark

import org.openjdk.jmh.annotations._

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
class CsvInputBenchmark extends ExampleData {
  @Benchmark
  def parseTuples = parse[Tuple](tupleCsv)
}
