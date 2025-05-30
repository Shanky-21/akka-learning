package com.akka.persistence

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import com.akka.persistence.CounterActorCommands._
import com.akka.persistence.CounterActorEvents._

object PersistentCounterActor {
  def props(id: String): Props = Props(new PersistentCounterActor(id))
}

class PersistentCounterActor(id: String) extends PersistentActor with ActorLogging {

  // 1. Define the unique persistence ID for this actor
  override def persistenceId: String = s"counter-$id"

  // 2. Internal state of the actor
  private var state: CounterActorState = CounterActorState()

  private def updateState(event: CounterEvent): Unit = {
    state = state.updated(event)
  }

  // How many events before taking a snapshot
  private val snapshotInterval = 5
  private var eventsSinceLastSnapshot = 0

  // 3. Command Handler: Processes incoming commands
  override def receiveCommand: Receive = {
    case Increment(value) =>
      log.info(s"Received Increment($value) command. Current state: $state")
      if (value <= 0) {
        log.warning("Increment value must be positive.")
        sender() ! s"Error: Increment value must be positive. Value was $value."
      } else {
        val event = Incremented(value)
        // Persist the event. The handler updates state AFTER successful persistence.
        persist(event) { e =>
          updateState(e)
          eventsSinceLastSnapshot += 1
          log.info(s"Persisted Incremented(${e.value}). New state: $state. Events since last snapshot: $eventsSinceLastSnapshot")
          sender() ! s"Incremented by ${e.value}. Current value: ${state.currentValue}"
          // Optionally take snapshot
          // if (eventsSinceLastSnapshot >= snapshotInterval) {
          //   self ! TakeSnapshotPlease // Or call saveSnapshot directly
          // }
        }
      }

    case Decrement(value) =>
      log.info(s"Received Decrement($value) command. Current state: $state")
      if (value <= 0) {
        log.warning("Decrement value must be positive.")
        sender() ! s"Error: Decrement value must be positive. Value was $value."
      } else if (state.currentValue - value < 0) {
        log.warning(s"Decrement would result in negative value. Current: ${state.currentValue}, Decrement: $value")
        sender() ! s"Error: Cannot decrement by $value. Current value is ${state.currentValue}."
      } else {
        val event = Decremented(value)
        persist(event) { e =>
          updateState(e)
          eventsSinceLastSnapshot += 1
          log.info(s"Persisted Decremented(${e.value}). New state: $state. Events since last snapshot: $eventsSinceLastSnapshot")
          sender() ! s"Decremented by ${e.value}. Current value: ${state.currentValue}"
        }
      }

    case GetCurrentValue =>
      log.info(s"Received GetCurrentValue command. Replying with: ${state.currentValue}")
      sender() ! state.currentValue

    case PrintState => // For debugging
      log.info(s"Current actor state ($persistenceId): $state, lastSequenceNr: $lastSequenceNr")
      sender() ! state // Send state back for assertion or observation

    case TakeSnapshotPlease =>
      log.info(s"Attempting to save snapshot for state: $state, sequence number: $lastSequenceNr")
      saveSnapshot(state) // Save the current state as a snapshot

    case SaveSnapshotSuccess(metadata) =>
      log.info(s"Snapshot saved successfully: $metadata")
      eventsSinceLastSnapshot = 0
      // Optionally, delete old events/snapshots after successful new snapshot
      // deleteMessages(metadata.sequenceNr - 1) // Delete messages up to previous one
      // deleteSnapshots(SnapshotSelectionCriteria.Latest.withMaxSequenceNr(metadata.sequenceNr -1))


    case SaveSnapshotFailure(metadata, cause) =>
      log.error(cause, s"Snapshot save failed: $metadata")

    // Any other messages (e.g. String responses from sender()!)
    case s: String => log.info(s"PersistentCounterActor received simple string: $s")
  }

  // 4. Recovery Handler: Replays events from journal to rebuild state
  override def receiveRecover: Receive = {
    case event: CounterEvent =>
      log.info(s"Recovering with event: $event. Current state before: $state")
      updateState(event)
      eventsSinceLastSnapshot +=1 // Keep track during recovery too for snapshot interval logic if needed
      log.info(s"State after recovery event: $state. LastSequenceNr: $lastSequenceNr")

    case SnapshotOffer(metadata, snapshot: CounterActorState) =>
      log.info(s"Recovering from snapshot: $metadata, snapshot state: $snapshot")
      state = snapshot
      eventsSinceLastSnapshot = 0 // Reset counter as we loaded a snapshot
      log.info(s"State after snapshot recovery: $state")

    case RecoveryCompleted =>
      log.info(s"Recovery completed for $persistenceId. Final state: $state. Events since last snapshot: $eventsSinceLastSnapshot")
      // Perform any actions after recovery is complete, e.g., schedule tasks
      // if (eventsSinceLastSnapshot >= snapshotInterval) {
      //   self ! TakeSnapshotPlease // Decide if a snapshot is immediately needed post-recovery
      // }

    case other =>
      log.warning(s"Received unknown message during recovery: $other")
  }

  // (Optional) Override recovery to customize behavior
  // override def recovery: Recovery = super.recovery

  override def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit = {
    log.error(cause, s"Failed to persist event $event with sequence number $seqNr.")
    // Consider stopping the actor or implementing a retry/error strategy
    super.onPersistFailure(cause, event, seqNr)
  }

  override def onPersistRejected(cause: Throwable, event: Any, seqNr: Long): Unit = {
    log.error(cause, s"Persistence of event $event with sequence number $seqNr was rejected by the journal.")
    // This typically indicates a validation error in the journal plugin or a misconfiguration
    super.onPersistRejected(cause, event, seqNr)
  }
}