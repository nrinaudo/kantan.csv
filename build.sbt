lazy val root = Project(id = "scala-csv",
                        base = file(".")).aggregate(core, scalazStream, scalacheck).settings(packagedArtifacts := Map.empty)

lazy val core = project dependsOn(scalacheck % "test->test")

lazy val scalazStream = Project(id   = "scalaz-stream",
                                base = file("scalaz-stream")) dependsOn(core, scalacheck % "test->test")

lazy val scalacheck = project

lazy val docs = project dependsOn(core, scalazStream)
