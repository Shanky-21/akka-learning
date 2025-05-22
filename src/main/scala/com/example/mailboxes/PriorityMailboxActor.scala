package com.example.mailboxes

import akka.actor.{Actor, ActorLogging, Props}
import com.example.mailboxes.MailboxDemoMessages._

object PriorityMailboxActor {
  def props: Props = Props[PriorityMailboxActor]()
}

class PriorityMailboxActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case task: PrioritizedWork =>
      log.info(s"PriorityActor processing task ID ${task.id} with priority ${task.priority}: '${task.task}' (Thread: ${Thread.currentThread().getName})")
      // Simulate some work
      Thread.sleep(100)
      sender() ! TaskProcessed(task.id, s"Processed priority ${task.priority} task: ${task.task}")

    case other =>
      log.warning(s"PriorityActor received unknown message: $other")
  }

  override def preStart(): Unit = {
    log.info("PriorityMailboxActor started.")
  }
}