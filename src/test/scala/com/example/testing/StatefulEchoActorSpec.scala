package com.akka.testing

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe} // TestProbe for collaborator
 // Import messages
import com.akka.testing.StatefulEchoActor._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._

class StatefulEchoActorSpec extends TestKit(ActorSystem("StatefulEchoActorSpecSystem")) // 1. Create ActorSystem
  with ImplicitSender     // 2. ImplicitSender makes `testActor` the sender
  with AnyWordSpecLike    // 3. ScalaTest style
  with Matchers           // 4. ScalaTest matchers (e.g., should be)
  with BeforeAndAfterAll { // 5. For cleanup

  // Shutdown the ActorSystem after all tests are done
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system) // `system` is provided by TestKit
  }

  "A StatefulEchoActor" should {

    "reply with MessageSetAck when a message is set" in {
      val echoActor = system.actorOf(StatefulEchoActor.props)
      val message = "hello actor"
      echoActor ! SetMessage(message)

      // Expect a specific message back to the `testActor` (implicit sender)
      expectMsg(MessageSetAck(message))
      // Alternative: expect a message of a certain type
      // val ack = expectMsgClass(classOf[MessageSetAck])
      // ack.message should be (message)
    }

    "reply with the last set message when GetLastMessage is received" in {
      val echoActor = system.actorOf(StatefulEchoActor.props)
      val message1 = "first message"
      val message2 = "second message"

      echoActor ! SetMessage(message1)
      expectMsg(MessageSetAck(message1)) // Consume the ack

      echoActor ! SetMessage(message2)
      expectMsg(MessageSetAck(message2)) // Consume the ack

      echoActor ! GetLastMessage
      expectMsg(LastMessageResult(Some(message2)))
    }

    "reply with None if no message has been set" in {
      val echoActor = system.actorOf(StatefulEchoActor.props)
      echoActor ! GetLastMessage
      expectMsg(LastMessageResult(None))
    }

    "forward a message to a target actor" in {
      val echoActor = system.actorOf(StatefulEchoActor.props)
      val targetProbe = TestProbe("target") // Create a TestProbe to act as the target
      val messageToForward = "please forward this"

      // Send the ForwardTo message, providing the TestProbe's ref as the target
      echoActor ! ForwardTo(targetProbe.ref, messageToForward)

      // The TestProbe should have received the forwarded message.
      // Note: `testActor` (implicit sender) will NOT receive anything for this command.
      targetProbe.expectMsg(messageToForward)

      // Verify that our main testActor (implicit sender) did not receive the forwarded message
      expectNoMessage(200.millis) // Or some other small duration
    }

    "handle messages within a specified time" in {
      val echoActor = system.actorOf(StatefulEchoActor.props)
      within(500.millis) { // Max duration for the operations within this block
        echoActor ! SetMessage("quick test")
        expectMsg(MessageSetAck("quick test"))
      }
    }
  }
}