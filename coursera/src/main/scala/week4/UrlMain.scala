package week4

import akka.actor.{ReceiveTimeout, Props, Actor}
import scala.concurrent.duration._

/**
  * Created by weili on 16-1-14.
  */
class UrlMain extends Actor {
  import Receptionist._

  val receptionist = context.actorOf(Props[Receptionist], "receptionist")

  receptionist ! Get("http://www.baidu.com")

  context.setReceiveTimeout(10 seconds)

  def receive = {
    case Result(url, set) =>
      println(set.toVector.sorted.mkString(s"Results for '$url':\n", "\n", "\n"))
    case Failed(url) =>
      println(s"Failed to fetch '$url'\n")
    case ReceiveTimeout =>
      context.stop(self)
  }

  override def postStop(): Unit = {
    WebClient.shutdown()
  }
}
