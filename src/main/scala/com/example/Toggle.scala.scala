package com.example

import akka.actor.{Actor, ActorLogging, Props}

object Toggle {

  def props: Props = Props[Toggle]()

  // messages
  case object DoToggle // Command to toggle the state
  case object GetState // Command to query the current state

  final case class State(isOnline: Boolean) // Reply message

}

class Toggle extends Actor with ActorLogging {

  import Toggle._

  // Initial behavior is 'offline'
  override def receive: Receive = offline

  // Behavior when the toggle is OFF
  def offline: Receive = {

    case DoToggle => 
      log.info("Toggle received DoToggle command - Toggling to ONLINE")
      context.become(online) // change behavior to online
      sender() ! State(true) // reply with the new state

    case GetState => 
      log.info("Toggle is offline - replying with current state(false)")
      sender() ! State(false)

  }

  def online: Receive = {

    case DoToggle => 
      log.info("Toggle received DoToggle. Becoming offline ...")
      context.become(offline) // switch to offline behavior
      sender() ! State(false) // reply with the new state

    case GetState => 
      log.info("Toggle is online - replying with current state(true)")
      sender() ! State(true)

  }

}
