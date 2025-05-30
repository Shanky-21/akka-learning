package com.akka.testing

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object StatefulEchoActor {
  def props: Props = Props[StatefulEchoActor]()

  // Incoming Messages
  case class SetMessage(message: String)
  case object GetLastMessage
  case class ForwardTo(target: ActorRef, message: Any)

  // Outgoing Messages (Replies)
  case class MessageSetAck(message: String)
  case class LastMessageResult(message: Option[String])
  // No specific ack for ForwardTo, the message is just forwarded
}

class StatefulEchoActor extends Actor with ActorLogging {
  import StatefulEchoActor._

  private var lastMessage: Option[String] = None

  override def receive: Receive = {
    case SetMessage(msg) =>
      log.info(s"Setting message to: '$msg'")
      lastMessage = Some(msg)
      sender() ! MessageSetAck(msg)

    case GetLastMessage =>
      log.info(s"Replying with last message: $lastMessage")
      sender() ! LastMessageResult(lastMessage)

    case ForwardTo(target, msg) =>
      log.info(s"Forwarding message '$msg' to ${target.path.name}")
      target.forward(msg) // Use forward to preserve original sender if needed by target
                           // If original sender doesn't matter to target, `target ! msg` is also fine.
  }
}