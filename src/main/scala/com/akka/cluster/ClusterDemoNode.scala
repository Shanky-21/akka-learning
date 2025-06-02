package com.akka.cluster // Or your correct package name

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.io.StdIn

object ClusterDemoNode { // No longer extends App

  def main(args: Array[String]): Unit = { // Standard main method
    if (args.isEmpty) {
      println("Usage: ClusterDemoNode <config-file-name-without-extension e.g., application-node1>")
      System.exit(1)
    }

    val configFile = args(0)
    println(s"Starting cluster node with config: $configFile.conf")

    val config = ConfigFactory.load(configFile)
    val system = ActorSystem("ClusterDemoSystem", config)
    println(s"ActorSystem ${system.name} started successfully")

    // Assuming ClusterListenerActor is in the same package
    system.actorOf(ClusterListenerActor.props, "clusterListener")

    println(s"\n>>> Node configured with ${config.getString("akka.remote.artery.canonical.port")} <<<")
    println(">>> Press ENTER to shut down node <<<")
    StdIn.readLine()

    println("Shutting down node...")
    system.terminate()
  }
}