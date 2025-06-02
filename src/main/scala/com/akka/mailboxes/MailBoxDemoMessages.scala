package com.akka.mailboxes

import akka.dispatch.PriorityGenerator

object MailboxDemoMessages {

  // --- Messages for BoundedMailboxActor ---
  case class ProcessItem(id: Int, data: String)

  // --- Messages for PriorityMailboxActor ---
  // We need to ensure these messages can be prioritized by Akka's PriorityGenerator.
  // Option 1: Extend akka.dispatch.PriorityGenerator.MessageWithPriority
  // Option 2: Ensure messages have a `def priority: Int` method. (Simpler here)

  sealed trait PrioritizedWork {
    def id: Int
    def task: String
    def priority: Int // Lower value means higher priority for PriorityGenerator
  }

  case class HighPriorityTask(id: Int, task: String) extends PrioritizedWork {
    override val priority: Int = 0 // Highest
  }

  case class MediumPriorityTask(id: Int, task: String) extends PrioritizedWork {
    override val priority: Int = 5 // Medium
  }

  case class LowPriorityTask(id: Int, task: String) extends PrioritizedWork {
    override val priority: Int = 10 // Lowest
  }

  case class UrgentAdminTask(id: Int, task: String) extends PrioritizedWork {
    override val priority: Int = -5 // Even higher than "High"
  }

  // Response message (generic)
  case class TaskProcessed(id: Int, message: String)
}