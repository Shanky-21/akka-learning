package com.akka

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorRef, AllForOneStrategy, OneForOneStrategy, Props, SupervisorStrategy, Terminated} // Import Terminated
import scala.concurrent.duration._
import com.akka.WorkerActor

object SupervisorActor {
  def props: Props = Props[SupervisorActor]()
  // Message to create a worker
  case object CreateWorker
  // Message to send a command to the worker
  case class ForwardToWorker(msg: Any)
}

class SupervisorActor extends Actor with ActorLogging {
  import SupervisorActor._
  var worker: Option[ActorRef] = None

  // Define the supervision strategy
  override val supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1.minute) { // Try 3 restarts in 1 minute
      case _: ArithmeticException      =>
        log.warning("Supervisor: Worker encountered ArithmeticException. Resuming worker.")
        Resume
      case _: NullPointerException     =>
        log.warning("Supervisor: Worker encountered NullPointerException. Restarting worker.")
        Restart
      case _: IllegalArgumentException =>
        log.warning("Supervisor: Worker encountered IllegalArgumentException. Stopping worker.")
        Stop
      case _: Exception                =>
        log.warning("Supervisor: Worker encountered some other Exception. Escalating to my supervisor.")
        Escalate
    }

  override def preStart(): Unit = {
    log.info(s"SupervisorActor preStart. Path: ${self.path}")
  }

  override def postStop(): Unit = {
    log.info(s"SupervisorActor postStop. Path: ${self.path}")
  }

  def receive: Receive = {
    case CreateWorker =>
      if (worker.isEmpty) {
        log.info("Supervisor: Creating worker actor...")
        val newWorker = context.actorOf(WorkerActor.props, "myWorker")
        context.watch(newWorker) // Watch the worker for Terminated messages
        worker = Some(newWorker)
        sender() ! newWorker // Send the worker ref back to the creator (DemoApp)
      } else {
        sender() ! worker.get // Send existing worker ref
      }

    case ForwardToWorker(msg) =>
      worker.fold(log.warning("Supervisor: No worker to forward message to."))(_ ! msg)

    case Terminated(actorRef) if worker.contains(actorRef) =>
      log.warning(s"Supervisor: GrandParent is sending Terminated message to Supervisor. Worker actor ${actorRef.path} has terminated. Clearing worker reference.")
      worker = None
      // Optionally, recreate the worker here if it's critical
      // self ! CreateWorker

    case msg =>
      log.info(s"SupervisorActor received unknown message: $msg")
  }
  log.info(s"SupervisorActor constructor called. Path: ${self.path}")
}