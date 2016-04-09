package kantan.csv.joda

import kantan.codecs.strings.joda.time._

/** Brings all joda time instances in scope.
  *
  * Note that this is a convenience - the exact same effect can be achieved by importing
  * `kantan.codec.strings.joda.time._`. The sole purpose of this is to keep things simple for users that don't want or
  * need to learn about kantan.csv's internals.
  *
  */
package object time extends JodaTimeInstances
