import play.PlayScala


// TODO Replace with your project's/module's name
name := """play-angular-require-seed"""

// TODO Set your organization here
organization := "your.organization"

// TODO Set your version here
version := "2.3.1"

// Scala Version, Play supports both 2.10 and 2.11
//scalaVersion := "2.10.4"
scalaVersion := "2.10.4"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

// Dependencies
libraryDependencies ++= Seq(
  cache,
  filters,
  // ReactiveMongo dependencies
  "org.reactivemongo" %% "reactivemongo" % "0.10.5.akka23-SNAPSHOT",
  // ReactiveMongo dependencies
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2"
)

unmanagedResourceDirectories in Assets += baseDirectory.value / "bower_components"

//
// Scala Compiler Options
// If this project is only a subproject, add these to a common project setting.
 //
scalacOptions ++= Seq(
  "-target:jvm-1.7",
  "-encoding", "UTF-8",
  "-deprecation", // warning and location for usages of deprecated APIs
  "-feature", // warning and location for usages of features that should be imported explicitly
  "-unchecked", // additional warnings where generated code depends on assumptions
  "-Xlint", // recommended additional warnings
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code"
)
