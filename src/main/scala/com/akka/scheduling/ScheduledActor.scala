package com.akka.scheduling

import akka.actor.{Actor, ActorLogging, Cancellable, Props, Timers}
import scala.concurrent.duration._
import java.time.LocalTime

object ScheduledActor {
  def props: Props = Props[ScheduledActor]()

  // Messages for scheduling
  case object TickOnce
  case object RecurringTick
  case class ScheduledMessage(text: String)
  case object CancelRecurring // Message to self to cancel recurring task
}

class ScheduledActor extends Actor with ActorLogging with Timers { // Timers trait can also be used for scheduling
  import ScheduledActor._
  import context.dispatcher // ExecutionContext for scheduler

  var recurringTaskCancellable: Option[Cancellable] = None
  var messageCount = 0

  override def preStart(): Unit = {
    log.info("ScheduledActor started. Will schedule some tasks.")

    // 1. Schedule a one-off message to self after 2 seconds
    context.system.scheduler.scheduleOnce(2.seconds, self, ScheduledMessage("Hello from one-off schedule!"))

    // 2. Schedule a recurring message to self
    //    Starts after 1 second, then every 3 seconds
    //    Using scheduleWithFixedDelay for better behavior if processing takes time
    recurringTaskCancellable = Some(
      context.system.scheduler.scheduleWithFixedDelay(
        1.second, // initialDelay
        3.seconds, // delay between completion of one and start of next
        self,
        RecurringTick
      )
    )
    log.info("Scheduled one-off and recurring tasks.")

    // 3. Example of using Timers trait for a simple periodic message (alternative)
    //    Timers are often preferred for actor-internal, cancellable ticks.
    //    They are automatically cancelled when the actor stops.
    // timers.startSingleTimer("MySingleTimerKey", TickOnce, 500.millis)
    // timers.startPeriodicTimer("MyPeriodicTimerKey", RecurringTick, 1.second)
  }

  override def receive: Receive = {
    case ScheduledMessage(text) =>
      log.info(s"Received ScheduledMessage at ${LocalTime.now()}: '$text'")

    case RecurringTick =>
      messageCount += 1
      log.info(s"Received RecurringTick #${messageCount} at ${LocalTime.now()}")
      if (messageCount >= 5) {
        log.info("RecurringTick limit reached (5). Sending CancelRecurring to self.")
        self ! CancelRecurring // Tell self to cancel
      }

    case CancelRecurring =>
      recurringTaskCancellable.foreach { task =>
        if (!task.isCancelled) {
          log.info("Cancelling the recurring task now.")
          task.cancel()
          log.info(s"Recurring task cancelled: ${task.isCancelled}")
        } else {
          log.info("Recurring task was already cancelled.")
        }
      }
      recurringTaskCancellable = None // Clear it

    case TickOnce => // If using Timers example
      log.info(s"Received TickOnce (from Timers trait) at ${LocalTime.now()}")

    case other =>
      log.warning(s"Received unknown message: $other")
  }

  override def postStop(): Unit = {
    log.info("ScheduledActor stopping. Ensuring recurring task is cancelled.")
    recurringTaskCancellable.foreach { task =>
      if (!task.isCancelled) {
        log.warning("Recurring task was not cancelled before postStop. Cancelling now.")
        task.cancel()
      }
    }
    log.info("ScheduledActor stopped.")
  }
}