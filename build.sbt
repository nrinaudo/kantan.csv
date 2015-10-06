lazy val root = Project(id = "tabulate", base = file("."))
  .aggregate(core, scalazStream, scalacheck)
  .settings(noPublishSettings:_*)


lazy val core = project dependsOn(scalacheck % "test->test")

lazy val scalazStream = Project(id   = "scalaz-stream",
                                base = file("scalaz-stream")) dependsOn(core, scalacheck % "test->test")

lazy val scalacheck = project

lazy val docs = project.dependsOn(core, scalazStream)
  .settings(unidocSettings:_*)
  .settings(noPublishSettings:_*)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

