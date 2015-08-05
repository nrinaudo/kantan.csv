name                := "scala-csv"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)

libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.3.0"

libraryDependencies += "org.scalatest"        %% "scalatest"  % "2.2.5"  % "test"

libraryDependencies += "org.scalacheck"       %% "scalacheck" % "1.12.2" % "test"


