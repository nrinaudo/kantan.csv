import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.SbtSite.SiteKeys._
import UnidocKeys._
import de.heikoseeberger.sbtheader.license.Apache2_0
import scala.xml.transform.{RewriteRule, RuleTransformer}



// - Dependency versions -----------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
val commonsCsvVersion        = "1.4"
val jacksonCsvVersion        = "2.8.2"
val kantanCodecsVersion      = "0.1.8-SNAPSHOT"
val kindProjectorVersion     = "0.8.1"
val macroParadiseVersion     = "2.1.0"
val opencsvVersion           = "3.8"
val productCollectionVersion = "1.4.3"
val scalaCsvVersion          = "1.3.3"
val scalatestVersion         = "3.0.0-M9"
val scalazStreamVersion      = "0.8.4"
val univocityVersion         = "2.2.1"



// - Common settings ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
// Basic build settings.
lazy val buildSettings = Seq(
  organization       := "com.nrinaudo",
  scalaVersion       := "2.11.8",
  crossScalaVersions := Seq("2.10.6", "2.11.8"),
  autoAPIMappings    := true
)

// Minimum set of compiler flags for sane development.
lazy val compilerOptions = Seq(
  "-deprecation",
  "-target:jvm-1.7",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture"
)

// Settings that should be enabled in all modules.
lazy val baseSettings = Seq(
  // Version-specific compiler options.
  scalacOptions ++= compilerOptions ++ (
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 11)) ⇒ Seq("-Ywarn-unused-import")
      case Some((2, 10)) ⇒ Seq("-Xdivergence211")
      case _             ⇒ Nil
    }
  ),

  // Disable -Ywarn-unused-imports in the console.
  scalacOptions in (Compile, console) ~= { _.filterNot(Set("-Ywarn-unused-import")) },

  // Standard resolvers.
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  ),

  // Copyright header.
  headers := Map("scala" → Apache2_0("2016", "Nicolas Rinaudo")),

  // Common dependencies.
  libraryDependencies ++= macroDependencies(scalaVersion.value),
  libraryDependencies += compilerPlugin("org.spire-math" % "kind-projector" % kindProjectorVersion cross CrossVersion.binary),

  // don't include scoverage as a dependency in the pom
  // this code was copied from https://github.com/mongodb/mongo-spark
  pomPostProcess := { (node: xml.Node) ⇒
    new RuleTransformer(
      new RewriteRule {
        override def transform(node: xml.Node): Seq[xml.Node] = node match {
          case e: xml.Elem
              if e.label == "dependency" && e.child.exists(child ⇒ child.label == "groupId" && child.text == "org.scoverage") ⇒ Nil
          case _ ⇒ Seq(node)
        }
      }).transform(node).head
  },

  // Exclude laws from code coverage.
  ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "kantan\\.csv\\.laws\\..*",

  // Speeds compilation up.
  incOptions := incOptions.value.withNameHashing(true)
)

// Settings for all modules that won't be published.
lazy val noPublishSettings = Seq(
  publish         := (),
  publishLocal    := (),
  publishArtifact := false
)

// Settings for all modules that will be published.
lazy val publishSettings = Seq(
  homepage := Some(url("https://nrinaudo.github.io/kantan.csv/")),
  licenses := Seq("Apache-2.0" → url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  apiURL := Some(url("https://nrinaudo.github.io/kantan.csv/api/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/nrinaudo/kantan.csv"),
      "scm:git:git@github.com:nrinaudo/kantan.csv.git"
    )
  ),
  pomExtra := <developers>
    <developer>
      <id>nrinaudo</id>
      <name>Nicolas Rinaudo</name>
      <url>http://nrinaudo.github.io</url>
    </developer>
  </developers>
)

// Base settings for all modules.
// Modules that shouldn't be published must also use noPublishSettings.
lazy val allSettings = buildSettings ++ baseSettings ++ publishSettings

// Platform specific list of dependencies for macros.
def macroDependencies(v: String): List[ModuleID] =
  ("org.scala-lang" % "scala-reflect" % v % "provided") :: {
    if(v.startsWith("2.10")) List(compilerPlugin("org.scalamacros" % "paradise" % macroParadiseVersion cross CrossVersion.full))
    else Nil
  }



// - root projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val root = Project(id = "kantan-csv", base = file("."))
  .settings(moduleName := "root")
  .settings(allSettings)
  .settings(noPublishSettings)
  .settings(
    initialCommands in console :=
    """
      |import kantan.csv._
      |import kantan.csv.ops._
      |import kantan.csv.generic._
      |import kantan.csv.joda.time._
    """.stripMargin
  )
  .enablePlugins(AutomateHeaderPlugin)
  .aggregate(core, cats, scalaz, scalazStream, laws, tests, docs, generic, benchmark, jackson, commons, opencsv, jodaTime)
  .dependsOn(core, generic, jodaTime)

lazy val tests = project
  .settings(allSettings)
  .settings(noPublishSettings)
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core, jackson, commons, opencsv, laws, cats, generic, jodaTime, scalaz, scalazStream, benchmark)
  .settings(libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest"                    % scalatestVersion    % "test",
    "com.nrinaudo"  %% "kantan.codecs-cats-laws"      % kantanCodecsVersion % "test",
    "com.nrinaudo"  %% "kantan.codecs-shapeless-laws" % kantanCodecsVersion % "test",
    "com.nrinaudo"  %% "kantan.codecs-joda-time-laws" % kantanCodecsVersion % "test",
    "com.nrinaudo"  %% "kantan.codecs-scalaz-laws"    % kantanCodecsVersion % "test"
  ))

lazy val docs = project
  .settings(allSettings)
  .settings(site.settings)
  .settings(site.preprocessSite())
  .settings(ghpages.settings)
  .settings(unidocSettings)
  .settings(
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(benchmark),
    apiURL := Some(url("http://nrinaudo.github.io/kantan.csv/api/")),
    scalacOptions in (ScalaUnidoc, unidoc) ++= Seq(
      "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
      "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
    )
  )
  .settings(libraryDependencies ++= macroDependencies(scalaVersion.value))
  .settings(tutSettings)
  .settings(tutScalacOptions ~= (_.filterNot(Set("-Ywarn-unused-import"))))
  .settings(
    site.addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), "api"),
    site.addMappingsToSiteDir(tut, "_tut"),
    git.remoteRepo := "git@github.com:nrinaudo/kantan.csv.git",
    ghpagesNoJekyll := false,
    includeFilter in makeSite := "*.yml" | "*.md" | "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" |
                                 "*.eot" | "*.svg" | "*.ttf" | "*.woff" | "*.woff2" | "*.otf"
  )
  .settings(noPublishSettings:_*)
  .dependsOn(core, scalazStream, laws, cats, scalaz, generic, jackson, commons, opencsv, jodaTime)

lazy val benchmark = project
  .settings(buildSettings)
  .settings(baseSettings)
  .settings(noPublishSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(JmhPlugin)
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
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs" % kantanCodecsVersion)

lazy val laws = project
  .settings(
    moduleName := "kantan.csv-laws",
    name       := "laws"
  )
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
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
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core, laws % "test")
  .settings(libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % jacksonCsvVersion)

lazy val commons = project
  .settings(
    moduleName := "kantan.csv-commons",
    name       := "commons"
  )
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core, laws % "test")
  .settings(libraryDependencies += "org.apache.commons" % "commons-csv" % commonsCsvVersion)

lazy val opencsv = project
  .settings(
    moduleName := "kantan.csv-opencsv",
    name       := "opencsv"
  )
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core, laws % "test")
  .settings(libraryDependencies += "com.opencsv" % "opencsv" % opencsvVersion)



// - shapeless projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val generic = project
  .settings(
    moduleName := "kantan.csv-generic",
    name       := "generic"
  )
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs-shapeless" % kantanCodecsVersion)



// - scalaz projects ---------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val scalaz = project
  .settings(
    moduleName := "kantan.csv-scalaz",
    name       := "scalaz"
  )
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs-scalaz" % kantanCodecsVersion)



// - scalaz-stream (soon to be FS2) projects ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val scalazStream = Project(id = "scalaz-stream", base = file("scalaz-stream"))
  .settings(
    moduleName := "kantan.csv-scalaz-stream",
    name       := "scalaz-stream"
  )
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(scalaz)
  .settings(libraryDependencies += "org.scalaz.stream" %% "scalaz-stream" % scalazStreamVersion)



// - cats projects -----------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val cats = project
  .settings(
    moduleName := "kantan.csv-cats",
    name       := "cats"
  )
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs-cats" % kantanCodecsVersion)



// - joda-time projects ------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
lazy val jodaTime = Project(id = "joda-time", base = file("joda-time"))
  .settings(
    moduleName := "kantan.csv-joda-time",
    name       := "joda-time"
  )
  .settings(allSettings)
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core)
  .settings(libraryDependencies += "com.nrinaudo" %% "kantan.codecs-joda-time" % kantanCodecsVersion)



// - Command alisases --------------------------------------------------------------------------------------------------
// ---------------------------------------------------------------------------------------------------------------------
addCommandAlias("runBench",    "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 -rf csv -rff benchmarks.csv")
addCommandAlias("runProfiler", "benchmark/jmh:run -i 10 -wi 5 -f 1 -t 1 -o profiler.txt -prof stack:detailLine=true;lines=5;period=1 kantan.csv.benchmark.*kantan.*")
addCommandAlias("validate", ";clean;scalastyle;test:scalastyle;coverage;test;coverageReport;coverageAggregate;docs/makeSite")
