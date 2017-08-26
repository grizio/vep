name := "vep"

version := "1.1.8"

scalaVersion := "2.12.2"

libraryDependencies += "com.typesafe" % "config" % "1.3.1"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.9"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.9"
libraryDependencies += "com.typesafe.play" % "anorm_2.12" % "2.5.3"
libraryDependencies += "ch.megard" %% "akka-http-cors" % "0.1.10"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"
libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1212"
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "2.5.0"
libraryDependencies += "org.apache.commons" % "commons-email" % "1.4"

lazy val installClient = TaskKey[Unit]("installClient", "Install all elements for client part")
lazy val buildClient = TaskKey[Unit]("buildClient", "Build client part")
lazy val prod = TaskKey[Unit]("prod", "Build the application for production usage")

installClient := {
  streams.value.log.info("Installing client")
  "npm i".!
}

buildClient := {
  streams.value.log.info("Building client")
  "npm run build".!
}

prod := {}

prod <<= prod.dependsOn(buildClient, compile in Compile)

enablePlugins(JavaAppPackaging)
mappings in (Compile, packageDoc) := Seq()