package com.example

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.pattern.{ask, after, AskTimeoutException}
import akka.util.Timeout
import com.example.SupervisorActor.{CreateWorker, ForwardToWorker}
import com.example.WorkerActor._ // Import worker messages

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}

object SupervisionDemo extends App {
  implicit val system: ActorSystem = ActorSystem("SupervisionSystem")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(5.seconds)

  val supervisor = system.actorOf(SupervisorActor.props, "supervisor")

  supervisor ! CreateWorker

  supervisor ? GetState

  println(s"--- DemoApp: Supervisor created --- ${supervisor.path}")

  // Non-blocking delay helper
  def delay(duration: FiniteDuration): Future[Unit] = {
    after(duration, system.scheduler)(Future.successful(()))
  }

  // Modified to return Future and handle ask timeout
  def queryWorkerState(workerOpt: Option[ActorRef], label: String, queryTimeout: Timeout = Timeout(2.seconds)): Future[Unit] = {
    workerOpt match {
      case Some(worker) =>
        (worker ? GetState).mapTo[WorkerState].map {
          case WorkerState(id, data) =>
            println(s"DemoApp ($label) - Worker (id: $id) state: '$data'")
        }.recover {
          case _: AskTimeoutException =>
            println(s"DemoApp ($label) - Timed out while querying worker state. Worker might be stopped or unresponsive.")
          case ex =>
            println(s"DemoApp ($label) - Failed to get worker state: ${ex.getMessage}")
        }
      case None =>
        println(s"DemoApp ($label) - No worker reference to query.")
        Future.successful(())
    }
  }

  // Modified to return Future and use non-blocking delay
  def sendToWorker(workerOpt: Option[ActorRef], msg: Any, label: String, postSendDelay: FiniteDuration = 1500.millis): Future[Unit] = {
    workerOpt match {
      case Some(_) => // We send via supervisor, so just need to know if we expect a worker to exist
        println(s"\n--- DemoApp: Sending '$label' ($msg) to worker via supervisor ---")
        supervisor ! ForwardToWorker(msg)
        delay(postSendDelay) // Give time for processing, potential failure, and supervisor reaction
      case None =>
        println(s"\n--- DemoApp: No worker expected for '$label' ($msg) ---")
        supervisor ! ForwardToWorker(msg) // Send anyway to see supervisor log "no worker"
        delay(postSendDelay)
    }
  }

  println("--- DemoApp: Starting simplified supervision demo ---")

  val scenarioFuture: Future[Unit] = for {
    _ <- Future.successful(println("--- DemoApp: Asking supervisor to create initial worker (Simplified Scenario) ---"))
    initialWorker <- (supervisor ? CreateWorker).mapTo[ActorRef].recoverWith {
      case ex =>
        println(s"DemoApp (Simplified): FATAL - Failed to create initial worker: ${ex.getMessage}")
        ex.printStackTrace() // Print stack trace for detailed error
        Future.failed(ex)
    }
    _ = println(s"DemoApp (Simplified): Initial Worker created: ${initialWorker.path}")
    // All other scenarios removed for this test
  } yield ()

  scenarioFuture.onComplete {
    case Success(_) =>
      println("\n--- DemoApp (Simplified): Scenario completed successfully ---")
      finish()
    case Failure(ex) =>
      // The recoverWith in the for-comprehension should ideally handle this for worker creation failure
      println(s"\n--- DemoApp (Simplified): Scenario overall failed: ${ex.getMessage} ---")
      // ex.printStackTrace() // Already printed in recoverWith if it was a creation failure
      finish()
  }

  def finish(): Unit = {
    println("\n--- DemoApp (Simplified): Terminating system ---")
    system.terminate()
    println("--- DemoApp (Simplified): System termination initiated ---")
  }

  Await.ready(system.whenTerminated, 30.seconds)
  println("--- DemoApp (Simplified): Main thread exiting after system termination or timeout ---")
}