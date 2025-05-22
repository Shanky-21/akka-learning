package com.example.mailboxes

// Add these imports for the anonymous DeadLetter listener actor
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, DeadLetter, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.example.mailboxes.MailboxDemoMessages._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object MailboxDemoApp extends App {
  println("Starting Mailbox Demo...")

  val config = ConfigFactory.load()
  val system = ActorSystem("MailboxSystem", config)

  implicit val ec: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(5.seconds)

  // --- Dead Letter Listener ---
  // The anonymous actor needs to correctly extend Actor and ActorLogging
  val deadLetterListener = system.actorOf(Props(new Actor with ActorLogging {
    override def receive: Receive = { // Actor.Receive or just Receive if Actor is in scope
      case d: DeadLetter => log.warning(s"DeadLetter captured: ${d.message} from ${d.sender} to ${d.recipient}")
    }
  }), "deadLetterListener")
  system.eventStream.subscribe(deadLetterListener, classOf[DeadLetter])
  println("DeadLetter listener subscribed.")

  // --- Bounded Mailbox Demo ---
  println("\n--- Bounded Mailbox Demo ---")
  val boundedActor: ActorRef = system.actorOf(
    BoundedMailboxActor.props.withDispatcher("bounded-mailbox-dispatcher"),
    "boundedActor"
  )
  println(s"BoundedActor created, using dispatcher 'bounded-mailbox-dispatcher' (check application.conf for mailbox capacity).")

  (1 to 15).foreach { i =>
    boundedActor ! ProcessItem(i, s"ItemData-$i")
    // Use println here as 'log' is not in scope for the App object
    println(s"Sent ProcessItem($i) to boundedActor.")
    if (i == 5) Thread.sleep(100)
  }

  println("Waiting for BoundedActor to process and to observe potential DeadLetters...")
  Thread.sleep(5000)


  // --- Priority Mailbox Demo ---
  println("\n--- Priority Mailbox Demo ---")
  val priorityActor: ActorRef = system.actorOf(
    PriorityMailboxActor.props.withDispatcher("priority-mailbox-dispatcher"),
    "priorityActor"
  )
  println(s"PriorityActor created, using dispatcher 'priority-mailbox-dispatcher'.")

  priorityActor ! LowPriorityTask(1, "Submit tax report")
  priorityActor ! HighPriorityTask(2, "Fix critical production bug")
  priorityActor ! MediumPriorityTask(3, "Review code pull request")
  priorityActor ! UrgentAdminTask(4, "System restart imminent - save work!")
  priorityActor ! LowPriorityTask(5, "Order new stationery")
  priorityActor ! HighPriorityTask(6, "Investigate security alert")

  println("Sent various priority tasks. Waiting for PriorityActor to process...")
  Thread.sleep(3000)


  // --- Shutdown ---
  println("\nTerminating ActorSystem...")
  system.terminate()
  println("Mailbox Demo Finished.")
}