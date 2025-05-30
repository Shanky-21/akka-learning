package com.akka

import akka.actor.{Actor, ActorLogging,ActorRef, Props}

object ForwarderActor {

  def props: Props = Props[ForwarderActor]()

  // Message to tell the ForwarderActor what to forward and to whom
  final case class ForwardMessage(content: Any, target: ActorRef)


}

class ForwarderActor extends Actor with ActorLogging {

  import ForwarderActor._

  override def receive: Receive = {

    case ForwardMessage(content, target) =>

        log.info(s"ForwarderActor received ForwardMessage for target ${target.path.name}. Forwarding '$content'")
        // The 'target' actor will see 'ForwarderActor' as the sender of 'content'
        target ! content


    case reply => 

      log.info(s"ForwarderActor received reply '$reply' from ${sender().path.name}")


  }

}