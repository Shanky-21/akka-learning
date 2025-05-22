# Akka Scheduling: Theory and Concepts

## 1. What is Akka Scheduler?

Akka's `Scheduler` is a utility that allows you to send messages to actors at a specific time in the future, or repeatedly at a fixed interval. It's a fundamental tool for implementing time-based logic, timeouts, periodic tasks, and reminders in actor systems.

-   **Provided by `ActorSystem`:** Each `ActorSystem` has its own scheduler instance, accessible via `system.scheduler`.
-   **Non-Blocking:** Scheduling a task is a non-blocking operation. The scheduler will execute the task on a separate thread from its own dispatcher (typically `akka.actor.default-scheduler-dispatcher`).
-   **Message-Based:** Scheduling typically involves sending a message to an actor. This aligns perfectly with the actor model – time events are just another type of message.

## 2. Key Scheduling Operations

The `Scheduler` provides several methods for scheduling tasks:

-   **`scheduleOnce(delay, receiver, message)`:**
    -   Sends the `message` to the `receiver` (an `ActorRef`) once after the specified `delay` (a `FiniteDuration`).
    -   Returns a `Cancellable`.

    ```scala
    import scala.concurrent.duration._
    import system.dispatcher // or another ExecutionContext for the scheduler

    val cancellable = system.scheduler.scheduleOnce(5.seconds, targetActor, "TickOnce")
    ```

-   **`schedule(initialDelay, interval, receiver, message)` (Classic Akka - Deprecated in Akka Typed, use `scheduleAtFixedRate` or `scheduleWithFixedDelay` instead for new code with Akka Typed, but still used in Classic):**
    -   Sends the `message` to the `receiver` repeatedly.
    -   The first message is sent after `initialDelay`.
    -   Subsequent messages are sent every `interval`.
    -   Returns a `Cancellable`.
    -   *Note on `schedule` for recurring tasks*: This method tries to maintain the `interval` between the *start times* of executions. If a task execution takes longer than the interval, the next task might start immediately after the previous one finishes, potentially leading to overlapping if not careful or if tasks are long.

    ```scala
    val recurringTask = system.scheduler.schedule(
      0.seconds,       // initialDelay
      1.minute,        // interval
      reportActor,     // receiver
      GenerateReport   // message
    )
    ```

-   **`scheduleWithFixedDelay(initialDelay, delay, receiver, message)` (Preferred for Classic Akka over `schedule` for many use cases, and a direct equivalent in Akka Typed):**
    -   Sends the `message` to the `receiver` repeatedly.
    -   The first message is sent after `initialDelay`.
    -   Subsequent messages are sent after the specified `delay` *has passed since the completion of the previous message processing by the actor*.
    -   This ensures a fixed delay *between* the end of one task and the start of the next, preventing tasks from piling up if processing takes time.
    -   Returns a `Cancellable`.

    ```scala
    val fixedDelayTask = system.scheduler.scheduleWithFixedDelay(
      5.seconds,       // initialDelay
      30.seconds,      // delay between completion and next start
      cleanupActor,
      PerformCleanup
    )(system.dispatcher) // ExecutionContext is often explicitly passed
    ```

-   **`scheduleAtFixedRate(initialDelay, interval, receiver, message)` (Alternative to `schedule` and direct equivalent in Akka Typed):**
    -   Similar to `schedule`, aims to run tasks at a fixed rate, meaning messages are sent every `interval` starting from `initialDelay`.
    -   If a task execution is delayed (e.g., actor is busy, or task takes long), subsequent executions might occur in rapid succession to "catch up" to the schedule, up to a certain limit. If executions are consistently late, the scheduler might skip some to get back on track.
    -   Returns a `Cancellable`.

    ```scala
    val fixedRateTask = system.scheduler.scheduleAtFixedRate(
      10.seconds,      // initialDelay
      1.hour,          // interval
      backupActor,
      StartBackup
    )(system.dispatcher) // ExecutionContext is often explicitly passed
    ```

## 3. `Cancellable`

-   All `schedule*` methods return a `Cancellable` object.
-   This object has a `cancel()` method, which can be called to prevent the scheduled message(s) from being sent.
-   It also has an `isCancelled()` method to check if it has already been cancelled.
-   It's important to manage these `Cancellable` instances, especially for recurring tasks, to stop them when they are no longer needed (e.g., when the actor that initiated them stops) to prevent resource leaks or unwanted messages.

## 4. `ExecutionContext`

-   The scheduler methods (especially `schedule`, `scheduleWithFixedDelay`, `scheduleAtFixedRate`) require an `ExecutionContext` to be in scope or passed explicitly. This context is used by the scheduler to run the internal logic that eventually sends the message.
-   Often, `system.dispatcher` (the default dispatcher of the actor system) is used.
-   You can also use `context.dispatcher` if scheduling from within an actor (though scheduling is often done from `system.scheduler` directly).

## 5. Use Cases

-   **Timeouts:** Schedule a message to an actor itself (e.g., `ReceiveTimeout`) or another actor to signal a timeout.
-   **Periodic Tasks:** Polling external resources, generating reports, sending heartbeats, performing cleanup.
-   **Reminders:** Sending follow-up messages.
-   **Delayed Actions:** Performing an action after a certain period.

## 6. Accuracy and Guarantees

-   Akka's scheduler provides "at-least-once" semantics for sending the scheduled message, assuming the `ActorSystem` is running and the receiver actor exists.
-   **Accuracy:** The scheduler aims for accuracy but is subject to the usual constraints of JVM thread scheduling and system load. It's generally accurate enough for most purposes but shouldn't be relied upon for hard real-time precision.
-   **Long-running tasks in actors:** If the actor receiving the scheduled message performs a very long-running, blocking operation within its `receive` method, it will delay the processing of subsequent (scheduled or regular) messages. Design actors to be non-blocking.

## 7. Best Practices

-   **Cancel When Done:** Always cancel recurring tasks when they are no longer needed (e.g., in an actor's `postStop` hook) to prevent "ghost" messages and potential resource leaks.
    ```scala
    class MyActor extends Actor {
      val tickTask: Cancellable = context.system.scheduler.scheduleWithFixedDelay(
        1.second, 1.second, self, "Tick"
      )(context.dispatcher)

      override def postStop(): Unit = {
        tickTask.cancel()
        super.postStop()
      }
      // ... receive method ...
    }
    ```
-   **Self-Scheduling:** Actors can schedule messages to `self`. This is a common pattern for periodic self-checks or internal state updates.
-   **Idempotency:** If there's a chance a scheduled task might be triggered multiple times due to system restarts or other failures (though less common with Akka's built-in scheduler itself), design the receiving actor's logic to be idempotent.
-   **Scheduler Dispatcher:** The scheduler itself runs on its own dispatcher (`akka.actor.default-scheduler-dispatcher`). For most applications, the default configuration for this dispatcher is sufficient.

Akka's scheduler is a simple yet powerful tool for managing time-based events within your actor system.