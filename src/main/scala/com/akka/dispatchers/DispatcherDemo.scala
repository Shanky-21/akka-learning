// src/main/scala/com/example/dispatchers/DispatcherDemo.scala
package com.akka.dispatchers

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.akka.dispatchers.FastActor.{FastTask, FastTaskResult}
import com.akka.dispatchers.SlowBlockingActor.{SlowTask, SlowTaskResult}
import com.akka.dispatchers.{FastActor, SlowBlockingActor}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import scala.concurrent.Await

object DispatcherDemo extends App {
  val system = ActorSystem("DispatcherSystem")
  implicit val ec: scala.concurrent.ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(20.seconds) // << INCREASED TIMEOUT

  val numFastTasks = 20
  val numSlowTasks = 5 // Number of slow actors in the pool, and tasks sent to them
  val blockingDuration = 2000L // 2 seconds for each slow task

  def runScenario(fastActor: ActorRef, slowActorRouter: ActorRef, scenarioName: String): Future[Unit] = {
    println(s"\n--- Starting Scenario: $scenarioName ---")
    val scenarioStartTime = System.currentTimeMillis()

    // Send all tasks quickly
    val fastFutures = (1 to numFastTasks).map { i =>
      val taskSentTime = System.currentTimeMillis()
      (fastActor ? FastTask(i, s"fast_payload_$i"))
        .mapTo[FastTaskResult]
        .map { res =>
          val timeTakenForThisTask = System.currentTimeMillis() - taskSentTime
          val timeSinceScenarioStart = System.currentTimeMillis() - scenarioStartTime
          // This println happens when the Future completes (i.e., reply received)
          println(f"[Fast-$i%02d] Received: '${res.result}'. Task processing took approx $timeTakenForThisTask%4dms. (At ${timeSinceScenarioStart}%5dms since scenario start)")
          s"[Fast-$i] (At $timeSinceScenarioStart)"
        }
        .recover { case e =>
          val timeSinceScenarioStart = System.currentTimeMillis() - scenarioStartTime
          val errorMessage = s"[Fast-$i%02d] FAILED: ${e.getMessage} (At $timeSinceScenarioStart%5dms since scenario start)"
          println(errorMessage)
          errorMessage
        }
    }

    val slowFutures = (1 to numSlowTasks).map { i =>
      val taskSentTime = System.currentTimeMillis()
      // Each message to the router will be sent to one of the slow actors in the pool
      (slowActorRouter ? SlowTask(i, blockingDuration))
        .mapTo[SlowTaskResult]
        .map { res =>
          val timeTakenForThisTask = System.currentTimeMillis() - taskSentTime
          val timeSinceScenarioStart = System.currentTimeMillis() - scenarioStartTime
          // This println happens when the Future completes
          println(f"[Slow-$i%02d] Received: '${res.message}'. Task processing took approx $timeTakenForThisTask%4dms. (At ${timeSinceScenarioStart}%5dms since scenario start)")
          s"[Slow-$i] (At $timeSinceScenarioStart)"
        }
        .recover { case e =>
          val timeSinceScenarioStart = System.currentTimeMillis() - scenarioStartTime
          val errorMessage = s"[Slow-$i%02d] FAILED: ${e.getMessage} (At $timeSinceScenarioStart%5dms since scenario start)"
          println(errorMessage)
          errorMessage
        }
    }

    val allTaskFutures = fastFutures ++ slowFutures
    val allResultsFuture = Future.sequence(allTaskFutures)

    allResultsFuture.map { _ => // We don't care about the aggregated results list for this print
      val endTime = System.currentTimeMillis()
      println(s"--- Scenario: $scenarioName ALL TASKS COMPLETED in ${endTime - scenarioStartTime} ms ---")
    }.recover {
      case e: Exception =>
        val endTime = System.currentTimeMillis()
        println(s"--- Scenario: $scenarioName FAILED (or some tasks timed out) after ${endTime - scenarioStartTime} ms: ${e.getMessage} ---")
    }
  }

  // Scenario 2: Slow actor on 'my-blocking-dispatcher'
  println("SCENARIO 2: Slow actor on 'my-blocking-dispatcher'")
  val fastActor_custom = system.actorOf(FastActor.props(), "fastActor-custom") // Still on default
  val slowActor_custom_pool = system.actorOf(
    RoundRobinPool(numSlowTasks)
      .props(SlowBlockingActor.props())
      .withDispatcher("my-blocking-dispatcher"), // Assign custom dispatcher
    "slowActorPool-custom"
  )
  val scenario2Completion = runScenario(fastActor_custom, slowActor_custom_pool, "Dedicated Blocking Dispatcher for Slow Actor")

  // Chain Scenario 1 to run after Scenario 2 completes, then terminate the system
  val allScenariosAndTerminationFuture: Unit = scenario2Completion.flatMap { _ =>
    println("\n\nSCENARIO 1: Both actors on default dispatcher")
    val fastActor_default = system.actorOf(FastActor.props(), "fastActor-default")
    val slowActor_default_pool = system.actorOf(
      RoundRobinPool(numSlowTasks).props(SlowBlockingActor.props()),
      "slowActorPool-default"
    )
    runScenario(fastActor_default, slowActor_default_pool, "Default Dispatcher for All")
  }.transformWith { scenarioResult =>
    scenarioResult match {
      case Success(_) => 
        println("\nAll scenarios attempted.")
        Future.successful(())
      case Failure(ex) => 
        println(s"\nAn error occurred during scenarios: ${ex.getMessage}")
        Future.failed(ex)
    }
    // Terminate the system and ensure this Future is the one we await
  }.onComplete { _ =>
    println("Actor system terminated.")
  }

  // Wait for all scenarios and system termination to complete before exiting
  // Await.result(allScenariosAndTerminationFuture, timeout.duration + 10.seconds) // Increased timeout for safety
}