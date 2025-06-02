package com.akka.cluster

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

object ClusterListenerActor {
  def props: Props = Props[ClusterListenerActor]()
}

class ClusterListenerActor extends Actor with ActorLogging {
  val cluster: Cluster = Cluster(context.system)

  // Subscribe to cluster changes
  override def preStart(): Unit = {
    log.info(s"ClusterListener subscribing to cluster events on ${cluster.selfAddress}")
    // initialStateMode = InitialStateAsEvents means we'll get events for current members too
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember], classOf[LeaderChanged])
  }

  // Unsubscribe from cluster changes when actor stops
  override def postStop(): Unit = {
    cluster.unsubscribe(self)
    log.info(s"ClusterListener unsubscribed from cluster events on ${cluster.selfAddress}")
  }

  override def receive: Receive = {
    case MemberJoined(member) =>
      log.info(s"Member joined: ${member.address} with roles ${member.roles}")
    case MemberUp(member) =>
      log.info(s"Member is Up: ${member.address} with roles ${member.roles}. This node is now part of the cluster.")
    case MemberLeft(member) =>
      log.info(s"Member is Leaving: ${member.address}")
    case MemberExited(member) =>
      log.info(s"Member Exited: ${member.address}")
    case MemberRemoved(member, previousStatus) =>
      log.info(s"Member Removed: ${member.address} after $previousStatus")
    case UnreachableMember(member) =>
      log.info(s"Member detected as Unreachable: ${member.address}")
    case ReachableMember(member) => // Only if a previously unreachable member becomes reachable again
      log.info(s"Member detected as Reachable again: ${member.address}")
    case LeaderChanged(leaderOption) =>
      leaderOption match {
        case Some(leaderAddress) => log.info(s"Cluster Leader changed. New leader is: $leaderAddress")
        case None                => log.info("Cluster Leader is currently None (e.g., during convergence).")
      }
    case event: MemberEvent => // Catch-all for other member events if needed
      log.info(s"Received other member event: $event")
  }
}