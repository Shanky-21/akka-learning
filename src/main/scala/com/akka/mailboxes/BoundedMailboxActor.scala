package com.akka.mailboxes

import akka.actor.{Actor, ActorLogging, Props, Timers} // Timers might not be used if Tick is removed
import com.akka.mailboxes.MailboxDemoMessages._

import scala.concurrent.duration._ // Only if Timers are used

object BoundedMailboxActor {
  def props: Props = Props[BoundedMailboxActor]()
  // private case object Tick // Not strictly needed if not logging mailbox size periodically
}

class BoundedMailboxActor extends Actor with ActorLogging { // Removed Timers if Tick is not used
  // import BoundedMailboxActor._ // Not needed if Tick is not used
  // timers.startPeriodicTimer("MailboxLogTimer", Tick, 500.millis) // Not needed

  override def receive: Receive = {
    case ProcessItem(id, data) =>
      // Removed problematic line:
      // val currentMailboxSize = context.asInstanceOf[akka.actor.ActorCell].mailbox.numberOfMessages
      log.info(s"BoundedActor processing item $id: '$data'. (Thread: ${Thread.currentThread().getName})")
      // Simulate work
      Thread.sleep(300) // Each item takes some time to process
      sender() ! TaskProcessed(id, s"Processed item $id: $data")

    // case Tick => // Not needed if not logging mailbox size
    //   log.info(s"BoundedActor periodic check.")
  }

  override def preStart(): Unit = {
    log.info("BoundedMailboxActor started.")
  }

  override def postStop(): Unit = {
    log.info("BoundedMailboxActor stopped.")
  }
}