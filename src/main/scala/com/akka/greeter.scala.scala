package com.akka

import akka.actor.{Actor, ActorLogging, Props}


// Define the message our greeter actor will understand

object Greeter {

  // props factory method
  def props(greetingPrefix: String): Props = Props(new Greeter(greetingPrefix))

  // messages
  final case class Greet(name: String)

  case object Greeted // A message to acknowledge the greet message

}

class Greeter(greetingPrefix: String) extends Actor with ActorLogging {

  import Greeter._ // import the messages from companion object

  override def receive: Receive = {

    case Greet(name) => 

    val message  = s"${greetingPrefix}, $name!"

    log.info(message)

  }

}