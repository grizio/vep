name := "vep"

version := "1.4.5"

scalaVersion := "2.13.3"

libraryDependencies += "com.typesafe" % "config" % "1.4.0"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.30"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.2.0"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.9"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.9"
libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.7"
libraryDependencies += "ch.megard" %% "akka-http-cors" % "1.1.0"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.16"
libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "3.5.0"
libraryDependencies += "org.apache.commons" % "commons-email" % "1.5"
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.6"
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.3.0"

enablePlugins(JavaAppPackaging)
mappings in (Compile, packageDoc) := Seq()
