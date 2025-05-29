ThisBuild / scalaVersion := "3.3.6" // Or a newer 2.13.x or 3.x version
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / organization := "Shashank-akka-learning"

val AkkaVersion = "2.6.20" 

lazy val root = (project in file("."))
  .settings(
    name := "akka-learning",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion, // For classic Akka Actors (we'll start here)
      // "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0", // For Akka Typed Actors (can explore later)
      "ch.qos.logback" % "logback-classic" % "1.2.11", // For logging

      // Add these for testing:
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
      "com.typesafe.akka" %% "akka-persistence" % AkkaVersion,

      "org.scalatest" %% "scalatest" % "3.2.18" % Test // Or a newer 3.2.x version
    )
  )