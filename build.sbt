name := """khachaturian"""

version := "1.0"

scalaVersion := "2.11.3"
//scalaVersion := "2.10.6"

//scalacOptions += "-target:jvm-1.8"
resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "org.jsoup" % "jsoup" % "1.8.3",
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "org.mapdb" % "mapdb" % "1.0.6").map(_.exclude("org.slf4j", "slf4j-simple"))

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

//enablePlugins(JavaAppPackaging,UniversalPlugin)
mainClass in Compile := Some("it.kdde.webanalysis.Khachaturian")