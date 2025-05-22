package com.example.routing

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import com.example.routing.WorkerActor.{Work, WorkComplete}

object MasterActor {
  def props(workerCount: Int): Props = Props(new MasterActor(workerCount))
  
  // Messages
  case class ProcessBatch(items: Seq[String])
  case class BatchComplete(results: Map[Int, String])
}

class MasterActor(workerCount: Int) extends Actor with ActorLogging {
  import MasterActor._
  
  // Create the workers
  private var router: Router = {
    val routees = Vector.fill(workerCount) {
      val worker = context.actorOf(WorkerActor.props)
      context.watch(worker) // Watch for termination
      ActorRefRoutee(worker)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
  
  // Keep track of pending work
  private var pendingWorks = Map.empty[Int, String]
  private var results = Map.empty[Int, String]
  private var originalSender: ActorRef = null
  
  override def receive: Receive = {
    case ProcessBatch(items) =>
      log.info(s"Received batch of ${items.size} items")
      originalSender = sender()
      
      // Distribute work among workers
      items.zipWithIndex.foreach { case (data, idx) =>
        pendingWorks += (idx -> data)
        router.route(Work(idx, data), self)
      }
      
    case WorkComplete(id, result, workerId) =>
      log.info(s"Work $id completed by worker $workerId: $result")
      pendingWorks -= id
      results += (id -> result)
      
      // If all work is complete, send back full results
      if (pendingWorks.isEmpty && originalSender != null) {
        originalSender ! BatchComplete(results)
        results = Map.empty
        originalSender = null
      }
      
    case Terminated(worker) =>
      // Worker terminated, remove from router
      router = router.removeRoutee(worker)
      
      // Create new worker if needed
      if (router.routees.size < workerCount) {
        val newWorker = context.actorOf(WorkerActor.props)
        context.watch(newWorker)
        router = router.addRoutee(newWorker)
      }
  }
}