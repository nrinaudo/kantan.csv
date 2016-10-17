// - Dependency versions -----------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
val commonsCsvVersion        = "1.4"
val jacksonCsvVersion        = "2.8.3"
val kantanCodecsVersion      = "0.1.10-SNAPSHOT"
val opencsvVersion           = "3.8"
val productCollectionVersion = "1.4.3"
val scalaCsvVersion          = "1.3.3"
val scalatestVersion         = "3.0.0-M9"
val scalazStreamVersion      = "0.8.4"
val univocityVersion         = "2.2.2"

kantanProject in ThisBuild := "csv"



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
    """.stripMargin
  )
  .aggregate(core, cats, scalaz, scalazStream, laws, tests, docs, generic, benchmark, jackson, commons, opencsv, jodaTime)
  .dependsOn(core, generic, jodaTime)

lazy val tests = project
  .enablePlugins(UnpublishedPlugin)
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
  .dependsOn(core, jackson, commons, opencsv, laws, cats, generic, jodaTime, scalaz, scalazStream, benchmark)
  .settings(libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest"                    % scalatestVersion    % "test",
    "com.nrinaudo"  %% "kantan.codecs-cats-laws"      % kantanCodecsVersion % "test",
    "com.nrinaudo"  %% "kantan.codecs-shapeless-laws" % kantanCodecsVersion % "test",
    "com.nrinaudo"  %% "kantan.codecs-joda-time-laws" % kantanCodecsVersion % "test",
    "com.nrinaudo"  %% "kantan.codecs-scalaz-laws"    % kantanCodecsVersion % "test"
  ))

lazy val docs = project
  .enablePlugins(DocumentationPlugin)
  .dependsOn(core, scalazStream, laws, cats, scalaz, generic, jackson, commons, opencsv, jodaTime)

lazy val benchmark = project
  .enablePlugins(UnpublishedPlugin, JmhPlugin)
  .dependsOn(core, jackson, opencsv, commons)
  .settings(libraryDependencies ++= Seq(
    "com.github.marklister" %% "product-collections" % productCollectionVersion,
    "com.univocity"         %  "univocity-parsers"   % univocityVersion,
    "com.github.tototoshi"  %% "scala-csv"           % scalaCsvVersion
  ))



// - core projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val core = project
  .settings(
    moduleName := "kantan.csv",
    name       := "core"
)
  .enablePlugins(PublishedPlugin)
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs" % kantanCodecsVersion)

lazy val laws = project
  .settings(
    moduleName := "kantan.csv-laws",
    name       := "laws"
  )
  .enablePlugins(PublishedPlugin)
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs-laws" % kantanCodecsVersion)



// - external engines projects -----------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val jackson = project
  .settings(
    moduleName := "kantan.csv-jackson",
    name       := "jackson"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % jacksonCsvVersion)

lazy val commons = project
  .settings(
    moduleName := "kantan.csv-commons",
    name       := "commons"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "org.apache.commons" % "commons-csv" % commonsCsvVersion)

lazy val opencsv = project
  .settings(
    moduleName := "kantan.csv-opencsv",
    name       := "opencsv"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.opencsv" % "opencsv" % opencsvVersion)



// - shapeless projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val generic = project
  .settings(
    moduleName := "kantan.csv-generic",
    name       := "generic"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs-shapeless" % kantanCodecsVersion)



// - scalaz projects ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val scalaz = project
  .settings(
    moduleName := "kantan.csv-scalaz",
    name       := "scalaz"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs-scalaz" % kantanCodecsVersion)



// - scalaz-stream (soon to be FS2) projects ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val scalazStream = Project(id = "scalaz-stream", base = file("scalaz-stream"))
  .settings(
    moduleName := "kantan.csv-scalaz-stream",
    name       := "scalaz-stream"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(scalaz)
  .settings(libraryDependencies += "org.scalaz.stream" %% "scalaz-stream" % scalazStreamVersion)



// - cats projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val cats = project
  .settings(
    moduleName := "kantan.csv-cats",
    name       := "cats"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs-cats" % kantanCodecsVersion)



// - joda-time projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val jodaTime = Project(id = "joda-time", base = file("joda-time"))
  .settings(
    moduleName := "kantan.csv-joda-time",
    name       := "joda-time"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs-joda-time" % kantanCodecsVersion)



// - Command alisases --------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
addCommandAlias("runBench",    "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 -rf csv -rff benchmarks.csv")
addCommandAlias("runProfiler", "benchmark/jmh:run -i 10 -wi 5 -f 1 -t 1 -o profiler.txt -prof stack:detailLine=true;lines=5;period=1 kantan.csv.benchmark.*kantan.*")
