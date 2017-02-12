import sbtunidoc.Plugin.UnidocKeys._

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
  .aggregate(core, cats, scalaz, laws, docs, generic, benchmark, jackson, commons, opencsv, jodaTime)
  .aggregateIf(java8Supported)(java8)
  .dependsOn(core, generic, jodaTime)

lazy val docs = project
  .enablePlugins(DocumentationPlugin)
  .settings(unidocProjectFilter in (ScalaUnidoc, unidoc) :=
    inAnyProject -- inProjectsIf(java8Supported)(java8) -- inProjects(benchmark)
  )
  .dependsOn(core, laws, cats, scalaz, generic, jackson, commons, opencsv, jodaTime)
  .dependsOnIf(java8Supported)(java8)

lazy val benchmark = project
  .enablePlugins(UnpublishedPlugin, JmhPlugin)
  .dependsOn(core, jackson, opencsv, commons)
  .settings(libraryDependencies ++= Seq(
    "com.github.marklister" %% "product-collections" % Versions.productCollection,
    "com.univocity"         %  "univocity-parsers"   % Versions.univocity,
    "com.github.tototoshi"  %% "scala-csv"           % Versions.scalaCsv,
    "org.scalatest"         %% "scalatest"           % Versions.scalatest % "test"
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
  .settings(libraryDependencies ++= Seq(
    "com.nrinaudo" %% "kantan.codecs" % Versions.kantanCodecs,
    "org.scalatest" %% "scalatest"    % Versions.scalatest     % "test"
  ))
  .laws("laws")

lazy val laws = project
  .settings(
    moduleName := "kantan.csv-laws",
    name       := "laws"
  )
  .enablePlugins(PublishedPlugin)
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
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
  .settings(libraryDependencies ++= Seq(
    "com.fasterxml.jackson.dataformat" %  "jackson-dataformat-csv" % Versions.jacksonCsv,
    "org.scalatest"                    %% "scalatest"              % Versions.scalatest % "test"
  ))

lazy val commons = project
  .settings(
    moduleName := "kantan.csv-commons",
    name       := "commons"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(libraryDependencies ++= Seq(
    "org.apache.commons" %  "commons-csv" % Versions.commonsCsv,
    "org.scalatest"      %% "scalatest"   % Versions.scalatest % "test"
  ))

lazy val opencsv = project
  .settings(
    moduleName := "kantan.csv-opencsv",
    name       := "opencsv"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(libraryDependencies ++= Seq(
    "com.opencsv"   %  "opencsv"   % Versions.opencsv,
    "org.scalatest" %% "scalatest" % Versions.scalatest % "test"
  ))



// - shapeless projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val generic = project
  .settings(
    moduleName := "kantan.csv-generic",
    name       := "generic"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(libraryDependencies ++= Seq(
    "com.nrinaudo"  %% "kantan.codecs-shapeless"      % Versions.kantanCodecs,
    "org.scalatest" %% "scalatest"                    % Versions.scalatest    % "test",
    "com.nrinaudo"  %% "kantan.codecs-shapeless-laws" % Versions.kantanCodecs % "test"
  ))



// - scalaz projects ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val scalaz = project
  .settings(
    moduleName := "kantan.csv-scalaz",
    name       := "scalaz"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(libraryDependencies ++= Seq(
    "com.nrinaudo"  %% "kantan.codecs-scalaz"         % Versions.kantanCodecs,
    "org.scalatest" %% "scalatest"                    % Versions.scalatest    % "test",
    "com.nrinaudo"  %% "kantan.codecs-scalaz-laws"    % Versions.kantanCodecs % "test"
  ))



// - cats projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val cats = project
  .settings(
    moduleName := "kantan.csv-cats",
    name       := "cats"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(libraryDependencies ++= Seq(
    "com.nrinaudo"  %% "kantan.codecs-cats"           % Versions.kantanCodecs,
    "org.scalatest" %% "scalatest"                    % Versions.scalatest    % "test",
    "com.nrinaudo"  %% "kantan.codecs-cats-laws"      % Versions.kantanCodecs % "test"
  ))



// - joda-time projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val jodaTime = Project(id = "joda-time", base = file("joda-time"))
  .settings(
    moduleName := "kantan.csv-joda-time",
    name       := "joda-time"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(libraryDependencies ++= Seq(
    "com.nrinaudo"  %% "kantan.codecs-joda-time"      % Versions.kantanCodecs,
    "org.scalatest" %% "scalatest"                    % Versions.scalatest    % "test",
    "com.nrinaudo"  %% "kantan.codecs-joda-time-laws" % Versions.kantanCodecs % "test"
  ))


// - java8 projects ----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val java8 = project
  .settings(
    moduleName    := "kantan.csv-java8",
    name          := "java8"
  )
  .enablePlugins(PublishedPlugin)
  .dependsOn(core, laws % "test")
  .settings(libraryDependencies ++= Seq(
    "com.nrinaudo"  %% "kantan.codecs-java8"      % Versions.kantanCodecs,
    "com.nrinaudo"  %% "kantan.codecs-java8-laws" % Versions.kantanCodecs % "test",
    "org.scalatest" %% "scalatest"                % Versions.scalatest    % "test"
  ))



// - Command alisases --------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
addCommandAlias("runBench",    "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 -rf csv -rff benchmarks.csv")
addCommandAlias("runProfiler", "benchmark/jmh:run -i 10 -wi 5 -f 1 -t 1 -o profiler.txt -prof stack:detailLine=true;lines=5;period=1 kantan.csv.benchmark.*kantan.*")
