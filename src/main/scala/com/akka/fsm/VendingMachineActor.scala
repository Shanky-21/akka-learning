// src/main/scala/com/example/fsm/VendingMachineActor.scala
package com.akka.fsm

import akka.actor.{Actor, ActorLogging, Props, Stash}

object VendingMachineActor {
  // Props
  def props(): Props = Props(new VendingMachineActor())

  // Messages
  case class Restock(count: Int)
  case object RequestItem

  // Replies / Events
  case object ItemDispensed
  case object SoldOut
  case object MachineError
  case object RestockSuccessful
}

class VendingMachineActor extends Actor with ActorLogging with Stash {
  import VendingMachineActor._

  override def receive: Receive = outOfStock // Initial state

  def outOfStock: Receive = {
    case Restock(count) if count > 0 =>
      log.info(s"Restocking with $count items.")
      context.become(readyToDispense(count))
      sender() ! RestockSuccessful
      unstashAll() // Process any stashed requests
    case Restock(_) =>
      log.warning("Cannot restock with 0 or negative items.")
      sender() ! MachineError
    case RequestItem =>
      log.info("Requested item, but machine is out of stock.")
      sender() ! SoldOut
      stash() // Stash the request, maybe we'll get restocked soon
    case other =>
      log.warning(s"Received unknown message in OutOfStock state: $other")
  }

  def readyToDispense(currentStock: Int): Receive = {
    case RequestItem if currentStock > 0 =>
      val newStock = currentStock - 1
      log.info(s"Dispensing item. Stock remaining: $newStock")
      sender() ! ItemDispensed
      if (newStock == 0) {
        log.info("Machine is now out of stock.")
        context.become(outOfStock)
      } else {
        context.become(readyToDispense(newStock))
      }
    case RequestItem => // currentStock is 0, should not happen if logic is correct but good for safety
      log.warning("Requested item, but somehow in ReadyToDispense with 0 stock. Transitioning to OutOfStock.")
      sender() ! SoldOut
      context.become(outOfStock)
    case Restock(count) if count > 0 =>
      val totalStock = currentStock + count
      log.info(s"Restocking. Current stock: $currentStock, adding: $count. Total: $totalStock")
      context.become(readyToDispense(totalStock))
      sender() ! RestockSuccessful
    case Restock(_) =>
      log.warning("Cannot restock with 0 or negative items.")
      sender() ! MachineError
    case other =>
      log.warning(s"Received unknown message in ReadyToDispense state: $other")
  }
}