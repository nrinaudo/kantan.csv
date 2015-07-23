package scalaz.stream

import java.io.{File, InputStream}

import com.nrinaudo.csv._

import scala.collection.mutable.ArrayBuffer
import scala.io.{Codec, Source}
import scalaz.concurrent.Task

package object csv {
  // - Unsafe sources --------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def unsafeRowsR(src: => Source, separator: Char): Process[Task, ArrayBuffer[String]] =
    io.resource(Task.delay(src))(src => Task.delay(src.close())) { src =>
      lazy val lines = unsafe(src, separator)
      Task.delay { if(lines.hasNext) lines.next() else throw Cause.Terminated(Cause.End) }
    }

  def unsafeRowsR(file: String, sep: Char)(implicit c: Codec): Process[Task, ArrayBuffer[String]] =
    unsafeRowsR(Source.fromFile(file), sep)

  def unsafeRowsR(file: File, sep: Char)(implicit c: Codec): Process[Task, ArrayBuffer[String]] =
      unsafeRowsR(Source.fromFile(file), sep)

  def unsafeRowsR(in: InputStream, sep: Char)(implicit c: Codec): Process[Task, ArrayBuffer[String]] =
        unsafeRowsR(Source.fromInputStream(in), sep)



  // - Safe sources ----------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  def rowsR(src: => Source, separator: Char): Process[Task, Vector[String]] =
    unsafeRowsR(src, separator).map(_.toVector)

  def rowsR(file: String, sep: Char)(implicit c: Codec): Process[Task, Vector[String]] =
    rowsR(Source.fromFile(file), sep)

  def rowsR(file: File, sep: Char)(implicit c: Codec): Process[Task, Vector[String]] =
    rowsR(Source.fromFile(file), sep)

  def rowsR(in: InputStream, sep: Char)(implicit c: Codec): Process[Task, Vector[String]] =
    rowsR(Source.fromInputStream(in), sep)
}
