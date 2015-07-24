// This file is kept to simplify import in IDE.
// For build, use buiuld.gradle instead.

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  "io.spray" %% "spray-json" % "1.3.+",
  "io.spray" %% "spray-can" % "1.3.+",
  "io.spray" %% "spray-routing" % "1.3.+",
  "com.typesafe" % "config" % "1.3.+",
  "com.typesafe.akka" %% "akka-actor" % "2.3.+",
  "com.typesafe.play" %% "anorm" % "2.4.+",
  "mysql" % "mysql-connector-java" % "5.1.+",
  "commons-validator" % "commons-validator" % "1.4.+",
  "org.apache.commons" % "commons-lang3" % "3.+",
  "org.slf4j" % "slf4j-nop" % "1.7.+",

  "io.spray" %% "spray-testkit" % "1.3.3",
  "org.specs2" %% "specs2" % "2.4.17"
)