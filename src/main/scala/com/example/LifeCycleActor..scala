package com.example

import akka.actor.{Actor, ActorLogging, Props}

object LifeCycleActor {

  def props: Props = Props[LifeCycleActor]()

  // messages
  case object GetStatus
  case class Status(message: String)

  case object CauseError // Message to cause the actor to crash

}

class LifeCycleActor extends Actor with ActorLogging {

  import LifeCycleActor._

  var internalState: String = "initialized"

  override def preStart(): Unit = {

    super.preStart()

    internalState = "Actor started"

    log.info(s"preStart(): Actor is starting. Initial state: ${internalState}. Path: ${self.path}")

    // Example create a child actor 

    // context.actorOf(Props[ChildActor], "childActor")

  }

  override def postStop(): Unit = {

    super.postStop()

    internalState = "Actor stopped"

    log.info(s"postStop(): Actor is stopping. Final state: ${internalState}. Path: ${self.path}")
    // Example: Clean up resources
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {

    log.error(reason, s"preRestart(): Actor is about to restart due to [${reason.getMessage}] while processing [${message.getOrElse("No message")}]")
    // Custom pre-restart logic before children are stopped and postStop is called
    // For example, send a last gasp message
    // context.parent ! "I'm restarting!"
    super.preRestart(reason, message) // IMPORTANT: This calls postStop and stops children by default


  }

  override def postRestart(reason: Throwable): Unit = {

    // Note: internalState would be reset by the constructor to "Initialized" for the NEW instance
    log.info(s"postRestart(): Actor has been restarted due to [${reason.getMessage}]. Current state before preStart: '$internalState'")
    super.postRestart(reason) // IMPORTANT: This calls preStart by default
    // internalState will be "Actor Started" after super.postRestart -> preStart
    log.info(s"postRestart(): Actor state after preStart (via super.postRestart): '$internalState'")


  }

   override def receive: Receive = {
    case GetStatus =>
      sender() ! Status(s"Current state is: '$internalState'")
    case CauseError =>
      log.warning("CauseError message received. Simulating an error...")
      throw new RuntimeException("Simulated error for restart demo!")
    case msg =>
      log.info(s"Received unknown message: $msg")
      unhandled(msg)
  }

  log.info(s"LifecycleActor constructor called. Path: ${self.path}") // Path is available here



}

