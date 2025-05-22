package com.example

import akka.actor.{ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.example.Toggle._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

object ToggleDemo extends App {
  

  val actorSystem = ActorSystem("ToggleDemo") 

  implicit val timeout: Timeout = Timeout(3.seconds)

  val toggle = actorSystem.actorOf(Toggle.props, "toggle")

  println(s"Sending DoToggle command ...")

  def queryState(actorName: String): Unit = {

    (toggle ? GetState).mapTo[State].onComplete {

      case Success(State(isOnline)) => 
        println(s"$actorName is online: $isOnline")
      case Failure(e) => 
        println(s"Error getting state for $actorName: ${e.getMessage}")
    }
    Thread.sleep(1000) // Give ask time to complete and print before next action

  }

  println("--- Initial State ---")
  queryState("Toggle")

  println("\n --- Toggling ON --- \n")

  // Instead of: toggle ! DoToggle
  (toggle ? DoToggle).mapTo[State].onComplete { // Or mapTo[Any] if DoToggle doesn't always reply with State
    case Success(State(isOnline)) =>
      println(s"DoToggle successful, new state is: ${if (isOnline) "Online" else "Offline"}")
    case Failure(ex) =>
      println(s"DoToggle (ask) failed: ${ex.getMessage}")
  }

  Thread.sleep(500)

  queryState("Toggle")

  println("\n --- Toggling OFF --- \n")

  (toggle ? DoToggle).mapTo[State].onComplete { // Or mapTo[Any] if DoToggle doesn't always reply with State
    case Success(State(isOnline)) =>
      println(s"DoToggle successful, new state is: ${if (isOnline) "Online" else "Offline"}")
    case Failure(ex) =>
      println(s"DoToggle (ask) failed: ${ex.getMessage}")
  }

  Thread.sleep(500)

  queryState("Toggle")

  println("\n --- Toggling ON again --- \n")

  (toggle ? DoToggle).mapTo[State].onComplete { // Or mapTo[Any] if DoToggle doesn't always reply with State
    case Success(State(isOnline)) =>
      println(s"DoToggle successful, new state is: ${if (isOnline) "Online" else "Offline"}")
    case Failure(ex) =>
      println(s"DoToggle (ask) failed: ${ex.getMessage}")
  }

  Thread.sleep(500)

  queryState("Toggle")

  Thread.sleep(1000)

  actorSystem.terminate()

}

