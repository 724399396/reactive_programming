package week4

import akka.actor.{Props, ActorRef, Actor, ActorLogging}
import scala.concurrent.duration._

/**
  * Created by weili on 16-1-14.
  */
object Controller {
  case class Check(links: String, depth: Int)
  case class Result(result: Set[String])
  case object Timeout
}

class Controller extends Actor with ActorLogging {
  import Controller._

  var cache = Set.empty[String]
  var children = Set.empty[ActorRef]

  import context.dispatcher
  context.system.scheduler.scheduleOnce(10 seconds, self, Timeout)

  def receive = {
    case Check(url,depth) =>
      log.debug("{} checking {}", depth, url)
      if (!cache(url) && depth > 0)
        children += context.actorOf(Props(new Getter(url, depth - 1)))
      cache += url
    case Getter.Done =>
      children -= sender
      if (children.isEmpty) context.parent ! Result(cache)
    case Timeout => children foreach(_ ! Getter.Abort)
  }
}