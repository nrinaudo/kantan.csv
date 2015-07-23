lazy val root = Project(id = "scala-csv",
                        base = file(".")).aggregate(core, scalazStream).settings(packagedArtifacts := Map.empty)

lazy val core = project

lazy val scalazStream = Project(id   = "scalaz-stream",
                                base = file("scalaz-stream")) dependsOn(core)
