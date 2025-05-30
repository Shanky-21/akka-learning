


#### What we are going to implementation Example Files


*   `BoundedMailboxActor.scala`: An actor to demonstrate a bounded mailbox.
*   `PriorityMailboxActor.scala`: An actor to demonstrate a priority mailbox.
*   `MailboxDemoMessages.scala`: For message definitions, especially for the priority mailbox.
*   `MailboxDemoApp.scala`: The main application.
*   Update `application.conf` for mailbox and dispatcher configurations.

**a. `src/main/resources/application.conf`**

Add these configurations (or merge with existing):

```hocon
# src/main/resources/application.conf

# ... (previous akka, loglevel, my-blocking-dispatcher configurations) ...

# Dispatcher configuration for an actor using a bounded mailbox
bounded-mailbox-dispatcher {
  type = Dispatcher // Can be any dispatcher type
  executor = "thread-pool-executor" // Or fork-join-executor
  thread-pool-executor {
    core-pool-size-min = 1
    core-pool-size-max = 2
  }
  # This dispatcher will use the specified mailbox type
  mailbox-type = "akka.dispatch.BoundedMailbox"
  mailbox-capacity = 5 # Keep it small for easy demonstration
  # Time to wait when trying to push a message to a full mailbox.
  # 0ms often means drop if full. A small positive value might block the sender briefly.
  mailbox-push-timeout-time = 10ms
}

# Dispatcher configuration for an actor using a priority mailbox
# UnboundedPriorityMailbox uses akka.dispatch.PriorityGenerator by default.
# PriorityGenerator expects messages to either:
# 1. Be an instance of akka.dispatch.PriorityGenerator.MessageWithPriority
# 2. Have a method `def priority: Int` (lower integer means higher priority).
priority-mailbox-dispatcher {
  type = Dispatcher
  executor = "thread-pool-executor"
  thread-pool-executor {
     core-pool-size-min = 1
     core-pool-size-max = 2
  }
  mailbox-type = "akka.dispatch.UnboundedPriorityMailbox"
  # No further mailbox config needed if using PriorityGenerator with standard message structure
}

# Optional: For more complex priority logic, you could define a custom mailbox
# that uses your own java.util.Comparator<akka.dispatch.Envelope>.
# my-custom-priority-mailbox-config {
#   mailbox-type = "com.example.mailboxes.MyCustomPriorityMailbox" // Your FQCN
#   // Any config your custom mailbox constructor needs
# }
# then a dispatcher would use: mailbox-type = "my-custom-priority-mailbox-config"

# To see dropped messages from bounded mailbox
akka.log-dead-letters = 10
akka.log-dead-letters-during-shutdown = off