package com.akka

import akka.actor.{Actor, ActorLogging, Props}

object EchoActor {
 
 // Props factory method
 def props: Props = Props(new EchoActor)

 final case class EchoMessage(message: String)

 // Message to acknowledge the echo message
 case object Echoed

}

class EchoActor extends Actor with ActorLogging {


  import EchoActor._


  override def receive: Receive = {

    case EchoMessage(message) =>
      log.info(s"Received message: $message from ${sender().path.name}")

      // Sends the message back to the original sender
      sender() ! Echoed

  }
  
  
}