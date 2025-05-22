# Akka Persistence: Theory and Concepts (Event Sourcing)

Akka Persistence enables stateful actors to persist their internal state by storing a sequence of **events** that have led to the current state, rather than persisting the state itself directly. This pattern is known as **Event Sourcing**.

## 1. What is Event Sourcing?

Instead of saving the current state of an entity (e.g., a user profile object), you save all the changes (events) that have happened to that entity.
-   **Events are Facts:** Events are immutable facts representing something that has occurred (e.g., `UserRegistered`, `EmailAddressChanged`, `OrderPlaced`).
-   **State Reconstruction:** The current state of an actor can be rebuilt by replaying these persisted events in order.
-   **Audit Trail:** The event log naturally provides a full audit trail of how an actor reached its current state.

## 2. Core Concepts in Akka Persistence

-   **`PersistentActor` (Classic Akka):**
    -   Actors that want to use Akka Persistence extend the `PersistentActor` trait (or `AbstractPersistentActor` in Java). In Akka Typed, this is `EventSourcedBehavior`.
    -   It provides the core functionality for persisting events and recovering state.

-   **`persistenceId`:**
    -   A unique identifier for a `PersistentActor` instance across incarnations.
    -   This ID is crucial because it links the actor instance to its stream of persisted events in the journal.
    -   It must be stable and unique (e.g., derived from an entity ID like `user-123`, `order-ABC`).

-   **Commands:**
    -   Plain messages received by the `PersistentActor` that represent an intent to change state (e.g., `RegisterUserCommand`, `ChangeEmailCommand`).
    -   Commands are first validated. If valid, they typically result in one or more events being persisted.

-   **Events:**
    -   Immutable data objects that represent a state change that *has already occurred and been validated*.
    -   Events are what get stored in the journal.
    -   After an event is successfully persisted, it is then applied to the actor's in-memory state.

-   **Journal (Event Store):**
    -   A database or storage backend where events are durably stored.
    -   Akka Persistence uses a pluggable journal architecture. Common journal plugins include:
        -   `akka-persistence-jdbc` (for SQL databases)
        -   `akka-persistence-cassandra` (for Apache Cassandra)
        -   `akka-persistence-inmemory` (for testing and development, **not for production**)
    -   Events are appended to the journal for a given `persistenceId`.

-   **Snapshots (Optimization):**
    -   For actors with very long event streams, replaying all events on recovery can become slow.
    -   Snapshots allow a `PersistentActor` to save its entire current state at a specific point (sequence number).
    -   During recovery, the actor first loads the latest snapshot and then replays only the events that occurred *after* that snapshot, speeding up recovery.

## 3. The Lifecycle of a `PersistentActor`

A `PersistentActor` has two main operational modes:

**a. Recovery Mode:**
-   When a `PersistentActor` starts (or restarts after a crash), it first enters recovery mode.
-   It queries the journal for events associated with its `persistenceId`.
-   It replays these persisted events by passing them to its `receiveRecover` method to rebuild its internal state.
-   If snapshots are used, it first tries to load the latest snapshot and then replays subsequent events.
-   Once all events (or events since the last snapshot) are replayed, the actor transitions to command processing mode.

**b. Command Processing Mode:**
-   After recovery, the actor is ready to receive and process new commands.
-   This is handled by its `receiveCommand` method.
-   When a command is processed:
    1.  **Validation:** The command is validated.
    2.  **Persist Event(s):** If valid, the actor calls `persist(event)(handler)` or `persistAll(events)(handler)`.
        -   The `event` is sent to the journal for asynchronous storage.
        -   The `handler` (a callback function) is executed *only after* the event has been successfully persisted.
        -   **Crucially, the actor's in-memory state should only be updated inside this `handler` callback.** This ensures that state changes only reflect successfully persisted events.
        -   The `handler` also receives the persisted event, allowing for state updates.
    3.  **Side Effects:** After persisting and updating state, the actor might perform side effects (e.g., sending replies, interacting with other actors).

## 4. `PersistentActor` Methods (Classic Akka)

-   **`persistenceId: String` (abstract):** Must be implemented to return the unique ID.

-   **`receiveCommand: Receive` (abstract):**
    -   Handles incoming commands when the actor is in normal operational mode.
    -   This is where you call `persist` or `persistAll`.

-   **`receiveRecover: Receive` (abstract):**
    -   Handles events replayed from the journal during recovery.
    -   Also handles `SnapshotOffer` if snapshots are used.
    -   **Important:** Do not perform side effects (like sending messages to other actors) during recovery, as these side effects would have already happened when the events were originally processed. Only update internal state.

-   **`persist[A](event: A)(handler: A => Unit): Unit`:**
    -   Persists a single event. The `handler` is called with the event after successful persistence.
    -   Updates to `lastSequenceNr` and internal state should happen in the `handler`.

-   **`persistAsync[A](event: A)(handler: A => Unit): Unit`:**
    -   Similar to `persist` but does not Stash other commands while the event is being persisted. Allows higher throughput but requires careful handling of state if commands can be processed out of order relative to event persistence. Generally, `persist` is safer and simpler to reason about for most use cases.

-   **`defer[A](event: A)(handler: A => Unit): Unit` (or `deferAsync`):**
    -   For actions that should happen after events are persisted but don't generate new events themselves (e.g., sending a reply). The handler is called after the event is persisted. If multiple `persist` calls are made, `defer` handlers are called after all corresponding events from those `persist` calls are successfully written.

-   **`lastSequenceNr: Long`:**
    -   The sequence number of the last event successfully persisted by this actor.

-   **`saveSnapshot(snapshot: Any): Unit`:**
    -   Saves a snapshot of the actor's current state.
    -   The `snapshot` message will be offered to `receiveRecover` during recovery via a `SnapshotOffer` message.

-   **`deleteMessages(toSequenceNr: Long): Unit`:**
    -   Logically deletes events from the journal up to (and including) `toSequenceNr`. Actual physical deletion depends on the journal plugin (some may only mark as deleted).
    -   Often used after a successful snapshot to clean up old events.

-   **`deleteSnapshots(criteria: SnapshotSelectionCriteria): Unit`:**
    -   Deletes snapshots matching the given criteria.

## 5. Journal and Snapshot Store Configuration

Plugins for journals and snapshot stores are configured in `application.conf`.

Example for `akka-persistence-inmemory` (for testing/dev):
```hocon
# For testing, using the in-memory journal and snapshot store.
# DO NOT USE THIS IN PRODUCTION.
akka.persistence.journal.plugin = "akka.persistence.journal.inmem"
akka.persistence.snapshot-store.plugin = "akka.persistence.snapshot-store.inmem"

# (Optional) In-memory journal configuration
# akka.persistence.journal.inmem {
#   # Class name of the plugin
#   class = "akka.persistence.journal.inmem.InMemoryJournal"
#   # Dispatcher for the plugin
#   plugin-dispatcher = "akka.actor.default-dispatcher"
# }

# (Optional) In-memory snapshot store configuration
# akka.persistence.snapshot-store.inmem {
#   class = "akka.persistence.snapshot-store.inmem.InMemorySnapshotStore"
#   plugin-dispatcher = "akka.actor.default-dispatcher"
# }