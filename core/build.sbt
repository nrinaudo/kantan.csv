name                := "scala-csv"

libraryDependencies += "org.scalatest"     %% "scalatest"     % "2.2.5"   % "test"

libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.3.0"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)

