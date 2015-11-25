//enablePlugins(ScalaJSPlugin)

import sbt._

import Keys._

//import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

organization := "com.ansvia.macros"

name := "ansvia-debug"

version := "0.0.7-SNAPSHOT"

description := "Ansvia debug macro"

scalaVersion := "2.11.6"

//crossScalaVersions := Seq("2.10.4")

resolvers ++= Seq(
	"Sonatype Releases" at "https://oss.sonatype.org/content/groups/scala-tools",
	"typesafe repo"   at "http://repo.typesafe.com/typesafe/releases",
	"Ansvia release repo" at "http://scala.repo.ansvia.com/releases",
	"Ansvia snapshot repo" at "http://scala.repo.ansvia.com/nexus/content/repositories/snapshots"
)

//libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-compiler" % _)

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)

// libraryDependencies ++= Seq(
//     "org.specs2" %% "specs2" % "1.14",
//     "ch.qos.logback" % "logback-classic" % "1.0.13"
// )

//enable this if eclipse plugin activated
//EclipseKeys.withSource := true


publishTo <<= version { (v:String) =>
    val ansviaRepo = "http://scala.repo.ansvia.com/nexus"
    if(v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at ansviaRepo + "/content/repositories/snapshots")
    else
        Some("releases" at ansviaRepo + "/content/repositories/releases")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

crossPaths := true

pomExtra := (
  <url>http://ansvia.com</url>
  <developers>
    <developer>
      <id>anvie</id>
      <name>Robin Sy</name>
      <url>http://www.mindtalk.com/u/robin</url>
    </developer>
  </developers>)
