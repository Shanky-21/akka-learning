package com.akka

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import com.akka.EchoActor
import com.akka.ForwarderActor.ForwardMessage 

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global // For future callback like  onComplete
import scala.util.{Failure, Success}
import akka.pattern.ask 
import com.akka.EchoActor.EchoMessage
import com.akka.EchoActor.Echoed

object MessagePassingDemo extends App {


  // 1. Create an ActorSystem
  val system = ActorSystem("MessagePassingDemo")
  println(s"ActorSystem created: ${system.name}")

  try {

    val echoActor = system.actorOf(EchoActor.props, "echoActor")
    val forwarderActor = system.actorOf(ForwarderActor.props, "forwarderActor")

    println(s"\n\nActors created: echo=${echoActor.path}, forwarder=${forwarderActor.path}\n\n")

    // Scenario 1: Direct tell to EchoActor ( sender will be 'deadLetters') ...
    println("\n\nScenario 1: Direct tell to EchoActor ( sender will be 'deadLetters')\n\n")
    echoActor ! EchoMessage("Hello Echo directly from MessagePassingDemo!")
    Thread.sleep(500)

    // Scenario 2: Using ForwarderActor to send message to EchoActor

    println("\n\nScenario 2: Using ForwarderActor to send message to EchoActor\n\n")
    forwarderActor ! ForwardMessage(EchoMessage("Hello Echo via ForwarderActor!"), echoActor)
    Thread.sleep(500)

    // Scenario 3: Using 'ask' with EchoActor
    println("\n\nScenario 3: Using 'ask' with EchoActor\n\n")
    implicit val timout: Timeout = Timeout(3.seconds)

    val futureResponse = echoActor ? EchoMessage("Ping with ask from MessagePassingDemo")

    futureResponse.mapTo[Echoed.type].onComplete {
      case Success(response) =>
        println(s"MessagePassingDemo (ask) received reply: $response")
      case Failure(exception) =>
        println(s"MessagePassingDemo (ask) failed:  ${exception.getMessage}")

    }
    

    println("Waiting for 'ask' response...")
    Thread.sleep(4000)


  } finally {


    // 4. Terminate the ActorSystem
    println("Terminating ActorSystem MessagePassingDemo...")
    system.terminate()

  }

}