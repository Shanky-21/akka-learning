package com.example

import akka.actor.{ActorSystem, Props}
import com.example.Greeter.Greet // Import the Greet message

object MainApp extends App {

  // 1. Create an ActorSystem
  //    An ActorSystem is a heavyweight object that will allocate threads
  //    so create it carefully, once per application.
  val system = ActorSystem("MyAkkaSystem")
  println(s"ActorSystem created: ${system.name}")

  try {

    // 2. Create an instance of the Greeter actor
    //    "Props" is a configuration class to specify options for the creation of actors.
    //    We use the props factory method from the Greeter companion object.
    val greeterActor = system.actorOf(Greeter.props("Hello"), "greeterActor")
    // "greeterActor" is the name of this specific actor instance. It must be unique among siblings.

    println(s"Greeter actor created: ${greeterActor.path}")

    // 3. Send messages to the actor
    //    The `!` method (pronounced "tell") sends a message asynchronously.
    greeterActor ! Greet("Scala")
    greeterActor ! Greet("Akka")
    greeterActor ! "SomeOtherMessage" // This will be an unhandled message

    // Actors process messages asynchronously.
    // We need to wait a bit for messages to be processed before terminating the system.
    println("Messages sent. Waiting for actors to process...")
    Thread.sleep(1000) // Simple wait for demonstration. In real apps, use more robust coordination.

  } finally {
    // 4. Terminate the ActorSystem
    //    This will stop all actors and release resources.
    println("Terminating ActorSystem...")
    system.terminate()
  }


}