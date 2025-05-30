package com.akka

import akka.actor.{ActorSystem, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.akka.Counter._ // import messages and props

import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

object CounterDemo extends App {


  val actorSystem = ActorSystem("CounterDemo")

  implicit val timeout: Timeout = Timeout(3.seconds)

  // actor instance created
  val counter = actorSystem.actorOf(Counter.props, "counter")

  println(s"Sending Increment message to counter ...")

  counter ! Increment // counter = 1
  counter ! Increment // counter = 2
  counter ! Increment // counter = 3

  println(s"Sending Decrement message ...")

  counter ! Decrement // counter = 2

  // let the message be processed

  Thread.sleep(500)

  println(s"Asking counter for current count ...")

  (counter ? GetCount).onComplete {
    case Success(count) => println(s"Current count: $count")
    case Failure(e) => println(s"Error getting count: ${e.getMessage}")
  }




  actorSystem.terminate()




}