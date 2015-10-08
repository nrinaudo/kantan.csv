lazy val root = Project(id = "tabulate", base = file("."))
  .aggregate(core, scalazStream, laws, tests, docs)
  .settings(noPublishSettings:_*)


lazy val core = project

lazy val scalazStream = Project(id = "scalaz-stream", base = file("scalaz-stream"))
  .dependsOn(core, laws % "test")

lazy val scalaz = project.dependsOn(core, laws % "test")

lazy val laws = project.dependsOn(core)

lazy val tests = project.dependsOn(laws % "test").settings(noPublishSettings:_*)

lazy val docs = project.dependsOn(core, scalazStream)
  .settings(unidocSettings:_*)
  .settings(noPublishSettings:_*)

lazy val noPublishSettings = Seq(
  publish         := (),
  publishLocal    := (),
  publishArtifact := false
)
