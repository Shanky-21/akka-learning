package com.example.routing

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.routing.{FromConfig, RoundRobinPool}
import akka.util.Timeout
import com.example.routing.WorkerActor.{Work, WorkComplete}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

// Master actor to coordinate the work
object RouterMaster {
  def props(router: ActorRef): Props = Props(new RouterMaster(router))
  case class ProcessWork(items: Seq[String])
  case class WorkResults(results: Map[Int, String])
}

class RouterMaster(router: ActorRef) extends Actor with ActorLogging {
  import RouterMaster._
  
  private var pendingWork = Map.empty[Int, String]
  private var results = Map.empty[Int, String]
  private var originalSender: ActorRef = null
  
  override def receive: Receive = {
    case ProcessWork(items) =>
      log.info(s"Processing batch of ${items.size} items")
      originalSender = sender()
      
      items.zipWithIndex.foreach { case (data, idx) =>
        pendingWork += (idx -> data)
        router ! Work(idx, data)
      }
      
    case WorkComplete(id, result, workerId) =>
      log.info(s"Work $id completed by worker $workerId: $result")
      pendingWork -= id
      results += (id -> result)
      
      if (pendingWork.isEmpty && originalSender != null) {
        originalSender ! WorkResults(results)
        results = Map.empty
        originalSender = null
      }
  }
}

// Main application object
object BuiltInRouterDemo extends App {
  val system = ActorSystem("BuiltInRouterDemo")
  implicit val timeout: Timeout = Timeout(10.seconds)
  
  // Create routers using built-in pool routers
  
  // 1. Router created programmatically
  val programmaticRouter = system.actorOf(
    RoundRobinPool(5).props(WorkerActor.props),
    "programmaticRouter"
  )
  
  // 2. Router created from configuration
  // val configBasedRouter = system.actorOf(
  //   FromConfig.props(WorkerActor.props),
  //   "configBasedRouter"
  // )
  
  // Create masters that will use the routers
  val master1 = system.actorOf(RouterMaster.props(programmaticRouter), "master1")
  // val master2 = system.actorOf(RouterMaster.props(configBasedRouter), "master2")
  
  // Test data
  val workItems = (1 to 10).map(i => s"task-$i")
  
  println("\n=== Testing Programmatic Router ===")
  (master1 ? RouterMaster.ProcessWork(workItems)).mapTo[RouterMaster.WorkResults].onComplete {
    case Success(results) =>
      println("\nProgrammatic Router Results:")
      results.results.toSeq.sortBy(_._1).foreach { case (id, result) =>
        println(s"Task $id: $result")
      }
      
      // After programmatic router finishes, test config-based router
      // testConfigRouter()
      
    case Failure(ex) =>
      println(s"Programmatic Router Error: ${ex.getMessage}")
      system.terminate()
  }
  
  // def testConfigRouter(): Unit = {
  //   println("\n=== Testing Config-Based Router ===")
  //   (master2 ? RouterMaster.ProcessWork(workItems)).mapTo[RouterMaster.WorkResults].onComplete {
  //     case Success(results) =>
  //       println("\nConfig-Based Router Results:")
  //       results.results.toSeq.sortBy(_._1).foreach { case (id, result) =>
  //         println(s"Task $id: $result")
  //       }
  //       println("\nAll tests complete, terminating system...")
  //       system.terminate()
        
  //     case Failure(ex) =>
  //       println(s"Config-Based Router Error: ${ex.getMessage}")
  //       system.terminate()
  //   }
  // }
  
  // Keep JVM alive until tests complete
  Thread.sleep(10000)
}