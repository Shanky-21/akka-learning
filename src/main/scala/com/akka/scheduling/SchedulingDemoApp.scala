package com.example.scheduling

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.io.StdIn

object SchedulingDemoApp extends App {
  println("Starting Scheduling Demo...")

  val config = ConfigFactory.load() // No special config needed from application.conf for basic scheduler
  val system = ActorSystem("SchedulingSystem", config)

  // Create the actor that will handle scheduled messages
  val scheduledActor: ActorRef = system.actorOf(ScheduledActor.props, "scheduledActor")
  println(s"ScheduledActor created at path: ${scheduledActor.path}")

  // The ScheduledActor internally schedules messages to itself in its preStart.
  // We will just observe its logs.

  println("\nScheduledActor is running and has set up its own schedules.")
  println("Observe logs for 'ScheduledMessage' (one-off) and 'RecurringTick'.")
  println("The recurring tick will run 5 times and then cancel itself.")
  println("The demo will run for about 20 seconds to observe.")

  // Let the system run for a while to see scheduled messages.
  // For a longer demo, you might use StdIn.readLine()
  try {
    // The recurring task runs 5 times with a 3-second interval after an initial 1-second delay.
    // Initial delay (1s) + (5 ticks * 3s interval) approx = 1s + 15s = 16s.
    // Add some buffer.
    Thread.sleep(20000) // 20 seconds
  } catch {
    case _: InterruptedException => println("Demo interrupted.")
  } finally {
    println("\nDemo duration finished or interrupted.")
    println("Stopping ScheduledActor (which should also cancel any tasks in its postStop)...")
    // system.stop(scheduledActor) // One way to stop
    scheduledActor ! PoisonPill     // Another way, actor will process PoisonPill then stop
    println("Sent PoisonPill to ScheduledActor.")

    println("Terminating ActorSystem...")
    system.terminate()
    println("Scheduling Demo Finished.")
  }
}