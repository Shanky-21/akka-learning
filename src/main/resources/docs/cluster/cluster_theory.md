# Akka Cluster: Theory and Concepts

Akka Cluster provides a fault-tolerant, decentralized, peer-to-peer based cluster membership service with no single point of failure or single point of bottleneck. It allows you to create a group of Akka `ActorSystem`s (nodes) that work together as a single unit.

## 1. What is Akka Cluster?

-   **Decentralized:** All nodes in a cluster are equal. There is no master node that manages the cluster.
-   **Membership Protocol:** Uses a gossip protocol to disseminate cluster state information (which nodes are up, down, joining, leaving) eventually consistently across all nodes.
-   **Failure Detection:** Employs failure detectors (e.g., an AccrualFailureDetector based on Phi Accrual) to determine if other nodes are unreachable.
-   **Location Transparency:** Actors can be addressed using their logical path (`akka://system@host:port/user/myActor`) regardless of whether they are local or remote, but Akka Cluster makes interacting with actors on *cluster nodes* more robust.
-   **Foundation for Other Tools:** Akka Cluster is the basis for other Akka distributed tools like Cluster Sharding, Cluster Singleton, Distributed Data, and Distributed Pub/Sub.

## 2. Key Concepts

-   **Node (Member):** An instance of an `ActorSystem` that has joined the cluster. Each node is uniquely identified by its address (protocol, system name, hostname, port).
-   **Seed Nodes:**
    -   A list of contact points for nodes wishing to join the cluster.
    -   A new node attempts to connect to the seed nodes to discover the current cluster state and join.
    -   Seed nodes are just regular Akka nodes that are configured as initial contact points; they don't have special roles beyond that.
    -   It's recommended to have at least 2-3 seed nodes for resilience.
    -   Once a node has joined, the seed node list is less critical for that node, as it learns about other members via gossip.

-   **Gossip Protocol:** How cluster membership state is disseminated. Each node periodically sends its view of the cluster to other random nodes. This ensures that eventually, all nodes converge on the same understanding of the cluster's membership.

-   **Vector Clocks:** Used internally to ensure consistent ordering of membership events across the cluster.

-   **Leader:**
    -   In each cluster (or data center, if using multi-DC), one node is elected as the "leader."
    -   The leader's primary responsibility is to manage node lifecycle transitions (e.g., officially moving a node from "joining" to "up", or from "unreachable" to "down").
    -   The leader is *not* a single point of failure. If the leader crashes, another node will be elected leader.
    -   Applications typically don't interact directly with the leader for application logic.

-   **Member States:** Nodes transition through several states:
    -   `Joining`: Trying to connect to seed nodes and join.
    -   `WeaklyUp`: A transitional state for new members during an initial convergence phase (especially in large clusters or across data centers). Application-specific cluster singletons/sharding might not start until nodes are `Up`.
    -   `Up`: A fully functional member of the cluster.
    -   `Leaving`: Gracefully exiting the cluster.
    -   `Exiting`: Shutting down after leaving.
    -   `Down`: Marked as permanently removed from the cluster by the leader (often after being `Unreachable` for a period).
    -   `Removed`: No longer part of the cluster.
    -   `Unreachable`: A node that cannot be contacted by other nodes (as determined by the failure detector). If it remains unreachable, the leader may eventually mark it as `Down`.

-   **Failure Detector:**
    -   Monitors liveness of other nodes. Akka uses a Phi Accrual Failure Detector by default.
    -   It provides a suspicion level (`phi`) about a node's reachability. When `phi` exceeds a configured threshold, the node is considered `Unreachable`.

-   **Split Brain Problem & Resolution (Downing Providers):**
    -   A "split brain" occurs when network partitions cause a cluster to split into two or more sub-clusters, each unaware of the others and potentially believing the other part is down. Each partition might elect its own leader.
    -   This can lead to inconsistent state if both partitions continue to operate.
    -   Akka Cluster requires a **Downing Provider** strategy to resolve this. The provider decides which nodes to mark as `Down` to heal the cluster. Common strategies:
        -   **Static Quorum:** Requires a minimum number of nodes to be present in a partition to remain operational. Smaller partitions are downed.
        -   **Keep Majority:** The partition with the majority of nodes stays up.
        -   **Split Brain Resolver (SBR) (from Akka Commercial/Lightbend Subscription, or community alternatives):** More sophisticated strategies (e.g., keep-oldest, keep-majority, static-quorum, down-if-alone).
        -   **`akka.cluster.auto-down-unreachable-after` (Built-in but **NOT RECOMMENDED FOR PRODUCTION**):** Automatically downs unreachable nodes after a timeout. Prone to causing issues if network glitches are temporary, as it can eagerly down nodes that could have recovered.

-   **Roles:**
    -   Nodes can be assigned "roles" (e.g., "frontend", "backend", "compute-worker").
    -   Roles allow you to group nodes for specific purposes. Actors can be deployed on nodes with specific roles.
    -   Useful for Cluster Singleton (run on nodes with a specific role) or Cluster Sharding (shard entities to nodes with specific roles).

## 3. Joining a Cluster

1.  Configure the `ActorSystem` to be part of a cluster (via `application.conf`).
    -   Set `akka.actor.provider = "cluster"`.
    -   Define `akka.remote.artery.canonical.hostname` and `akka.remote.artery.canonical.port` (or `akka.remote.classic.netty.tcp.hostname` / `port` if using classic remoting, though Artery is default and recommended).
    -   Configure `akka.cluster.seed-nodes` with the addresses of one or more seed nodes.
2.  When the `ActorSystem` starts, it will attempt to contact the seed nodes.
3.  Once connected to a seed node (or any existing member), it will go through the joining process.

## 4. Cluster Events

Actors can subscribe to cluster membership events to react to changes in the cluster topology:
-   `MemberUp`
-   `MemberJoined`
-   `MemberLeft`
-   `MemberExited`
-   `MemberRemoved`
-   `UnreachableMember`
-   `ReachableMember`
-   `LeaderChanged`
-   `RoleLeaderChanged`

```scala
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class MyClusterListener extends Actor {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) => log.info(s"Member is Up: ${member.address}")
    case UnreachableMember(member) => log.info(s"Member detected as unreachable: ${member.address}")
    // ... handle other events ...
  }
}