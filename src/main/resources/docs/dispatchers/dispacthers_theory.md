# Akka Dispatchers: Theory and Concepts

## 1. What is a Dispatcher?

In Akka, a **Dispatcher** is what makes an Actor "alive." It's an essential component responsible for running your actors. Key responsibilities include:

-   **Message Dispatching:** It takes messages from an actor's mailbox.
-   **Execution Management:** It executes the actor's message processing logic (`receive` method) on a thread.
-   **Thread Pool Management:** Each dispatcher is configured with and manages a pool of threads.

Essentially, when you send a message to an actor, the message goes into the actor's mailbox. The dispatcher, when it's the actor's turn and a thread is available, picks up the actor and a message from its mailbox, then executes the actor's behavior on one of its threads.

## 2. The Default Dispatcher

Every Akka `ActorSystem` comes with a **default dispatcher**.
-   **ID:** `akka.actor.default-dispatcher`
-   **Configuration:** Typically configured to use a `fork-join-executor`.
-   **Purpose:** Optimized for a large number of actors that perform short-lived, non-blocking, CPU-bound tasks.
-   **Shared Resource:** By default, all actors created within an `ActorSystem` share this single default dispatcher unless explicitly configured otherwise.

While convenient, sharing a single dispatcher can become a bottleneck if some actors perform long-running or blocking operations.

## 3. Why Use Custom Dispatchers?

Creating and assigning custom dispatchers to actors is crucial for building robust and performant Akka applications. The main reasons include:

-   **Isolation ("Bulkheading"):**
    -   Prevent "noisy neighbors." If some actors perform long-running tasks (e.g., database queries, network I/O, complex computations that block a thread), they can monopolize threads in the default dispatcher. This can starve other, more responsive actors, leading to poor performance or even deadlocks.
    -   Assigning blocking actors to a separate, dedicated dispatcher with its own thread pool isolates their impact from the rest of the system.

-   **Workload-Specific Optimization:**
    -   **CPU-Bound Actors:** Benefit from dispatchers like the default `fork-join-executor`, which is good at work-stealing and utilizing multiple cores.
    -   **I/O-Bound (Blocking) Actors:** Benefit from `thread-pool-executor` with a configurable, often fixed, number of threads. The number of threads can be tuned based on the expected concurrency of blocking operations.

-   **Resource Management and Prioritization (Indirect):**
    -   By allocating specific thread pools to different types of actors, you gain finer-grained control over how system resources (CPU, threads) are utilized.
    -   While dispatchers don't directly prioritize actors in terms of message processing order (that's more a mailbox concern), ensuring a critical set of actors has its own dispatcher guarantees they won't be starved for threads by less critical, blocking tasks.

-   **Tuning Throughput:**
    -   Dispatchers have a `throughput` setting. This defines how many messages an actor will process from its mailbox in one go before the thread is yielded back to the dispatcher (allowing other actors to run).
    -   Higher throughput is good for CPU-bound tasks.
    -   Lower throughput (e.g., 1) is often better for I/O-bound or very short tasks, as it promotes fairness and responsiveness by allowing the thread to be re-assigned more quickly.

## 4. Common Dispatcher Types

Akka offers several dispatcher implementations:

-   **`Dispatcher` (Default Type):**
    -   Event-based. Can be backed by `fork-join-executor` (default for `akka.actor.default-dispatcher`) or `thread-pool-executor`.
    -   Suitable for most actors, especially non-blocking ones.

-   **`PinnedDispatcher`:**
    -   Provides a dedicated thread for each actor that uses it. Each actor gets its own thread from the pool, and that thread is used exclusively by that actor.
    -   **Use Case:** When an actor *must* perform blocking operations and requires complete isolation, or if an actor manages a resource that is not thread-safe and must only be accessed by a single thread (e.g., some native libraries).
    -   **Caution:** Use sparingly! It can lead to thread starvation if many actors are pinned, as it consumes threads rapidly.

-   **`CallingThreadDispatcher`:**
    -   Runs the actor's message processing logic on the *caller's* thread (the thread that invoked `tell` or `ask`).
    -   **Use Cases:** Primarily for testing (e.g., to make actor interactions synchronous for easier assertion) or very specific, localized scenarios where you want to avoid thread hand-offs.
    -   **Caution:** Generally not recommended for production use as it can break the actor model's concurrency benefits and lead to unexpected blocking or deadlocks if not handled carefully. It can also lead to unbounded stack growth if actors call each other synchronously using this dispatcher.

## 5. Configuration

Dispatchers are typically configured in your `application.conf` file.

**Example: A custom blocking I/O dispatcher:**

```hocon
my-blocking-io-dispatcher {
  # Type of dispatcher
  type = Dispatcher

  # Executor to use: "fork-join-executor" or "thread-pool-executor"
  executor = "thread-pool-executor"

  # Configuration for the thread-pool-executor
  thread-pool-executor {
    core-pool-size-min = 5      # Minimum number of threads
    core-pool-size-max = 10     # Maximum number of threads
    keep-alive-time = "60s"     # Time idle threads wait before terminating
    # allow-core-timeout = off  # (Optional) Whether core threads can time out
    # queue-size = 1000         # (Optional) If using a bounded queue for tasks within the dispatcher itself
  }

  # How many messages an actor processes before yielding the thread.
  # Lower for I/O bound (e.g., 1) to increase responsiveness for other actors.
  # Higher for CPU bound.
  throughput = 1
}