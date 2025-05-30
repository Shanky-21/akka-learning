package com.akka

import akka.actor.{Actor, ActorLogging, Props}

object WorkerActor {
  def props: Props = Props[WorkerActor]()

  // Messages to simulate different errors
  case class Task(data: String)
  case object SimulateArithmeticError
  case object SimulateNullPointerError
  case object SimulateIllegalArgumentError
  case object SimulateOtherError
  case object GetState // To check its internal state after potential resume/restart
  final case class WorkerState(id: Int, dataProcessed: String)
}

class WorkerActor extends Actor with ActorLogging {
  import WorkerActor._

  private var dataProcessedInternally: String = "None"
  private val workerId = self.hashCode() // Unique per instance

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"WorkerActor (id: $workerId) preStart: Initializing. Path: ${self.path}")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info(s"WorkerActor (id: $workerId) postStop: Cleaning up. Path: ${self.path}")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.error(reason, s"WorkerActor (id: $workerId) preRestart: About to restart due to [${reason.getMessage}] on message [${message.getOrElse("N/A")}].")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info(s"WorkerActor (id: $workerId) postRestart: Restarted due to [${reason.getMessage}].")
    super.postRestart(reason) // This will call preStart again for the new instance
  }


  def receive: Receive = {
    case Task(data) =>
      dataProcessedInternally = data.toUpperCase
      log.info(s"WorkerActor (id: $workerId) processed task: '$data'. Internal state: '$dataProcessedInternally'")
      sender() ! WorkerState(workerId, dataProcessedInternally)

    case SimulateArithmeticError =>
      log.info(s"WorkerActor (id: $workerId) simulating ArithmeticException...")
      val x = 1 / 0 // Will throw ArithmeticException

    case SimulateNullPointerError =>
      log.info(s"WorkerActor (id: $workerId) simulating NullPointerException...")
      val s: String = null
      s.length // Will throw NullPointerException

    case SimulateIllegalArgumentError =>
      log.info(s"WorkerActor (id: $workerId) simulating IllegalArgumentException...")
      throw new IllegalArgumentException("Bad argument simulated")

    case SimulateOtherError =>
      log.info(s"WorkerActor (id: $workerId) simulating some other Exception...")
      throw new Exception("Some other generic error simulated")

    case GetState =>
      sender() ! WorkerState(workerId, dataProcessedInternally)
  }
  log.info(s"WorkerActor (id: $workerId) constructor called. Path: ${self.path}")
}