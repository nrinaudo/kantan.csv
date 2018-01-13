kantanProject in ThisBuild := "csv"
startYear in ThisBuild     := Some(2015)

// - root projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val root = Project(id = "kantan-csv", base = file("."))
  .settings(moduleName := "root")
  .enablePlugins(UnpublishedPlugin)
  .settings(
    initialCommands in console :=
      """
      |import kantan.csv._
      |import kantan.csv.ops._
      |import kantan.csv.generic._
      |import kantan.csv.joda.time._
      |import kantan.csv.refined._
    """.stripMargin
  )
  .aggregate(core, cats, scalaz, laws, docs, generic, benchmark, jackson, commons, jodaTime, refined, enumeratum)
  .aggregateIf(java8Supported)(java8)
  .dependsOn(core, generic, jodaTime, refined, enumeratum)

lazy val docs = project
  .enablePlugins(DocumentationPlugin)
  .settings(
    unidocProjectFilter in (ScalaUnidoc, unidoc) :=
      inAnyProject -- inProjectsIf(!java8Supported)(java8) -- inProjects(benchmark)
  )
  .dependsOn(core, laws, cats, scalaz, generic, jackson, commons, jodaTime, refined, enumeratum)
  .dependsOnIf(java8Supported)(java8)

lazy val benchmark = project
  .enablePlugins(UnpublishedPlugin, JmhPlugin)
  .dependsOn(core, jackson, commons)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.marklister" %% "product-collections"     % Versions.productCollection,
      "com.opencsv"           % "opencsv"                  % Versions.opencsv,
      "com.univocity"         % "univocity-parsers"        % Versions.univocity,
      "com.github.tototoshi"  %% "scala-csv"               % Versions.scalaCsv,
      "com.nrinaudo"          %% "kantan.codecs-scalatest" % Versions.kantanCodecs % "test"
    )
  )

// - core projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val core = project
  .settings(
    moduleName := "kantan.csv",
    name       := "core"
  )
  // TODO: disable when we upgrade to 2.12.3, which appears to fix this issue.
  // This is necessary because with scala 2.12.x, we use too many nested lambdas for deserialisation to succeed with the
  // "optimised" behaviour.
  .settings(scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((_, x)) if x == 12 ⇒ Seq("-Ydelambdafy:inline")
    case _                       ⇒ Seq.empty
  }))
  .enablePlugins(PublishedPlugin, spray.boilerplate.BoilerplatePlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.codecs"           % Versions.kantanCodecs,
      "com.nrinaudo" %% "kantan.codecs-scalatest" % Versions.kantanCodecs % "test"
    )
  )
  .laws("laws")

lazy val laws = project
  .settings(
    moduleName := "kantan.csv-laws",
    name       := "laws"
  )
  .enablePlugins(PublishedPlugin, spray.boilerplate.BoilerplatePlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs-laws" % Versions.kantanCodecs)

// - external engines projects -----------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val jackson = project
  .settings(
    moduleName := "kantan.csv-jackson",
    name       := "jackson"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv"   % Versions.jacksonCsv,
      "com.nrinaudo"                     %% "kantan.codecs-scalatest" % Versions.kantanCodecs % "test"
    )
  )

lazy val commons = project
  .settings(
    moduleName := "kantan.csv-commons",
    name       := "commons"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-csv"              % Versions.commonsCsv,
      "com.nrinaudo"       %% "kantan.codecs-scalatest" % Versions.kantanCodecs % "test"
    )
  )

// - shapeless projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val generic = project
  .settings(
    moduleName := "kantan.csv-generic",
    name       := "generic"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.codecs-shapeless"      % Versions.kantanCodecs,
      "com.nrinaudo" %% "kantan.codecs-scalatest"      % Versions.kantanCodecs % "test",
      "com.nrinaudo" %% "kantan.codecs-shapeless-laws" % Versions.kantanCodecs % "test"
    )
  )

// - scalaz projects ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val scalaz = project
  .settings(
    moduleName := "kantan.csv-scalaz",
    name       := "scalaz"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.codecs-scalaz"      % Versions.kantanCodecs,
      "com.nrinaudo" %% "kantan.codecs-scalatest"   % Versions.kantanCodecs % "test",
      "com.nrinaudo" %% "kantan.codecs-scalaz-laws" % Versions.kantanCodecs % "test"
    )
  )

// - cats projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val cats = project
  .settings(
    moduleName := "kantan.csv-cats",
    name       := "cats"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.codecs-cats"      % Versions.kantanCodecs,
      "com.nrinaudo" %% "kantan.codecs-scalatest" % Versions.kantanCodecs % "test",
      "com.nrinaudo" %% "kantan.codecs-cats-laws" % Versions.kantanCodecs % "test"
    )
  )

// - joda-time projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val jodaTime = Project(id = "joda-time", base = file("joda-time"))
  .settings(
    moduleName := "kantan.csv-joda-time",
    name       := "joda-time"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.codecs-joda-time"      % Versions.kantanCodecs,
      "com.nrinaudo" %% "kantan.codecs-scalatest"      % Versions.kantanCodecs % "test",
      "com.nrinaudo" %% "kantan.codecs-joda-time-laws" % Versions.kantanCodecs % "test"
    )
  )

// - java8 projects ----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val java8 = project
  .settings(
    moduleName := "kantan.csv-java8",
    name       := "java8"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.codecs-java8"      % Versions.kantanCodecs,
      "com.nrinaudo" %% "kantan.codecs-java8-laws" % Versions.kantanCodecs % "test",
      "com.nrinaudo" %% "kantan.codecs-scalatest"  % Versions.kantanCodecs % "test"
    )
  )

// - refined project ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val refined = project
  .settings(
    moduleName := "kantan.csv-refined",
    name       := "refined"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.codecs-refined"      % Versions.kantanCodecs,
      "com.nrinaudo" %% "kantan.codecs-refined-laws" % Versions.kantanCodecs % "test",
      "com.nrinaudo" %% "kantan.codecs-scalatest"    % Versions.kantanCodecs % "test"
    )
  )

// - enumeratum project ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val enumeratum = project
  .settings(
    moduleName := "kantan.csv-enumeratum",
    name       := "enumeratum"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.codecs-enumeratum"      % Versions.kantanCodecs,
      "com.nrinaudo" %% "kantan.codecs-enumeratum-laws" % Versions.kantanCodecs % "test",
      "com.nrinaudo" %% "kantan.codecs-scalatest"       % Versions.kantanCodecs % "test"
    )
  )

// - Command alisases --------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
addCommandAlias("runBench", "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 -rf csv -rff benchmarks.csv")
addCommandAlias(
  "runProfiler",
  "benchmark/jmh:run -i 10 -wi 5 -f 1 -t 1 -o profiler.txt -prof stack:detailLine=true;lines=5;period=1 kantan.csv.benchmark.*kantan.*"
)
