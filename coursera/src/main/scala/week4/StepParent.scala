package week4

import akka.actor.{Actor, Props, ActorRef}

/**
  * Created by weili on 16-1-16.
  */
class StepParent(child: Props, probe: ActorRef) extends Actor {
  context.actorOf(child, "child")
  def receive = {
    case msg => probe.tell(msg, sender)
  }
}
