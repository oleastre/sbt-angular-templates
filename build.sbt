sbtPlugin := true

organization := "org.databrary"

name := "sbt-angular-templates"

description := "sbt-web plugin to generate (compressed) angular template assets from html files"

homepage := Some(url("http://github.com/databrary/sbt-angular-templates"))

licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.1"

scalacOptions ++= Seq("-feature","-deprecation")

scalaSource in Compile := baseDirectory.value / "src"

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.0.2")

libraryDependencies ++= Seq(
  "com.googlecode.htmlcompressor" % "htmlcompressor" % "1.5.2"
)

publishMavenStyle := true

publishArtifact in Test := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:databrary/sbt-angular-templates.git</url>
    <connection>scm:git:git@github.com:databrary/sbt-angular-templates.git</connection>
  </scm>
  <developers>
    <developer>
      <id>dylex</id>
      <name>Dylan Simon</name>
    </developer>
  </developers>)
