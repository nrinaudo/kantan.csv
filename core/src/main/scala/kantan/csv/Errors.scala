/*
 * Copyright 2017 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.csv

/** Parent type for all errors that can occur while dealing with CSV data.
  *
  * [[ReadError]] is split into two main error types:
  *  - [[DecodeError]]: errors that occur while decoding a cell or a row.
  *  - [[ParseError]]: errors that occur while parsing raw data into CSV.
  */
sealed abstract class ReadError extends Exception with Product with Serializable

/** Parent type for all errors that can occur while decoding CSV data. */
sealed abstract class DecodeError extends ReadError

/** Declares all possible values of type [[DecodeError]]. */
object DecodeError {
  /** Error that occurs when attempting to access a CSV cell whose index is outside of its row's boundaries.
    *
    * @param index index that caused the issue.
    */
  final case class OutOfBounds(index: Int) extends DecodeError

  /** Error that occurs when attempting to decode a CSV cell or row into an incompatible type.
    *
    * A typical example of this would be to try and decode a CSV cell into an `Int` when its content is, say, `foobar`.
    */
  sealed case class TypeError(message: String) extends DecodeError {
    override final val getMessage = message
  }

  object TypeError {
    def apply(str: String, t: Throwable): TypeError = new TypeError(str) {
      override val getCause = t
    }

    def apply(t: Throwable): TypeError = TypeError(Option(t.getMessage).getOrElse("Type error"), t)
  }
}

sealed abstract class ParseError extends ReadError

object ParseError {
  final case class NoSuchElement() extends ParseError

  sealed case class IOError(message: String) extends ParseError {
    override final val getMessage = message
  }

  object IOError {
    def apply(str: String, t: Throwable): IOError = new IOError(str) {
      override val getCause = t
    }

    def apply(t: Throwable): IOError = IOError(Option(t.getMessage).getOrElse("IO error"), t)
  }
}
