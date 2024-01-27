ThisBuild / scalaVersion     := "2.12.7"
// 2.12.18 does not work (compile but run into classloader issue)
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.uzh.dast"


lazy val paradiseVersion = "2.1.0"

// cps
autoCompilerPlugins := true
addCompilerPlugin("org.scala-lang.plugins" % "scala-continuations-plugin_2.12.0" % "1.0.3")
libraryDependencies += "org.scala-lang.plugins" % "scala-continuations-library_2.12" % "1.0.3"
scalacOptions += "-P:continuations:enable"

// lms
//scalacOptions += "-Yvirtualize"

resolvers += Resolver.sonatypeRepo("snapshots")
libraryDependencies += "org.scala-lang.lms" %% "lms-core-macrovirt" % "0.9.0-SNAPSHOT"
libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value % "compile"
libraryDependencies += "org.scala-lang" % "scala-library" % scalaVersion.value % "compile"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % "compile"

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)

lazy val root = (project in file("."))
  .settings(
    name := "DifferentiableABS",
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
