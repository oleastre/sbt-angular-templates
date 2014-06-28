sbtPlugin := true

organization := "org.databrary.sbt"

name := "sbt-angular-templates"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-feature","-deprecation")

scalaSource in Compile := baseDirectory.value / "src"

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.0.2")

libraryDependencies ++= Seq(
  "com.googlecode.htmlcompressor" % "htmlcompressor" % "1.5.2"
)
