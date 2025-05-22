# Akka Mailboxes: Theory and Concepts

## 1. What is a Mailbox?

In Akka, every actor has its own **mailbox**. The mailbox is essentially a **message queue** that holds messages sent to the actor until the actor is ready to process them.

-   **Enqueueing:** When you use `tell` (`!`) or `ask` (`?`) to send a message to an actor, the Akka system enqueues this message into the target actor's mailbox.
-   **Dequeueing:** The dispatcher, when it schedules an actor to run on a thread, dequeues messages one by one (or in a batch, depending on dispatcher throughput) from the actor's mailbox and feeds them to the actor's `receive` method for processing.
-   **Per-Actor:** Each actor instance has its own private mailbox.

## 2. The Default Mailbox

By default, actors in Akka are equipped with an **unbounded, non-blocking, FIFO (First-In, First-Out) mailbox**.
-   **Unbounded:** It can theoretically grow indefinitely to hold any number of messages.
    -   *Advantage:* Simplicity, no messages are dropped due to a full queue (unless other limits are hit).
    -   *Disadvantage:* Can lead to `OutOfMemoryError` if a producer actor sends messages much faster than a consumer actor can process them, causing the mailbox to consume all available heap memory.
-   **FIFO:** Messages are processed in the order they are received.

## 3. Why Use Custom Mailboxes?

While the default mailbox is suitable for many scenarios, custom mailboxes offer more control over message handling, resource management, and application behavior:

-   **Bounded Mailboxes (Back-Pressure and Stability):**
    -   Limit the number of messages an actor's mailbox can hold.
    -   **Prevents OOMEs:** By restricting queue size, you prevent runaway message accumulation.
    -   **Implicit Back-Pressure:** When a bounded mailbox is full, attempts to send more messages might be:
        -   Dropped (sent to DeadLetters).
        -   Cause the sender to block for a timeout (if `mailbox-push-timeout-time` is configured).
        -   Signal an error.
    -   This signals to producing systems that the consumer is overwhelmed, a form of back-pressure.

-   **Priority Mailboxes (Message Urgency):**
    -   Allow certain messages to be processed before others, regardless of their arrival order.
    -   **Use Case:** Critical alerts, high-priority commands, or administrative tasks that need to jump the queue of regular processing.
    -   Requires a `java.util.Comparator` to determine message priority. Akka provides `akka.dispatch.PriorityGenerator` which can be used if messages have a `priority: Int` field/method (lower int means higher priority).

-   **Specialized Mailboxes:**
    -   Akka and third-party libraries might offer mailboxes with specific semantics, e.g., mailboxes optimized for single consumers or specific concurrency patterns.

## 4. How Mailboxes and Dispatchers Relate

-   An actor is associated with **one mailbox** and **one dispatcher**.
-   The **dispatcher** is responsible for taking an actor and executing its message loop.
-   When the dispatcher picks an actor, it pulls messages from **that actor's mailbox**.
-   The type of mailbox an actor uses is typically configured *via the dispatcher* it's assigned to, or directly when the actor is created (though less common for standard mailboxes).

## 5. Configuring Mailboxes

Mailboxes are primarily configured in `application.conf`. You typically define a mailbox type and then assign it to a dispatcher, or define a dispatcher that uses a specific mailbox type.

**a. Defining a Mailbox Type (less common for standard types, more for custom implementations):**
```hocon
my-custom-mailbox {
  mailbox-type = "com.example.MySpecialMailboxType" # FQCN of your mailbox class
  // ... other mailbox-specific settings
}