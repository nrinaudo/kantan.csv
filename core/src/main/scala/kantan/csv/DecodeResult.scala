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

import kantan.codecs.ResultCompanion

/** Provides convenience methods for creating instances of [[DecodeResult]]. */
object DecodeResult extends ResultCompanion.WithDefault[DecodeError] {
  override protected def fromThrowable(t: Throwable) = DecodeError.TypeError(t)

  /** Creates a new [[DecodeResult]] failure wrapping a [[DecodeError.OutOfBounds]] error. */
  def outOfBounds(index: Int): DecodeResult[Nothing] = failure(DecodeError.OutOfBounds(index))
  /** Creates a new [[DecodeResult]] failure wrapping a [[DecodeError.TypeError]] error. */
  def typeError(str: String): DecodeResult[Nothing] = failure(DecodeError.TypeError(str))
  /** Creates a new [[DecodeResult]] failure wrapping a [[DecodeError.TypeError]] error. */
  def typeError(e: Exception): DecodeResult[Nothing] = failure(DecodeError.TypeError(e))
}
