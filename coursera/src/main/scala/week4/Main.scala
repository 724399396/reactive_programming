package week4

import akka.actor.{Props, Actor}

/**
  * Created by weili on 16-1-13.
  */
class Main extends Actor {
  val count = context.actorOf(Props[Counter], "count")

  count ! "inc"
  count ! "inc"
  count ! "inc"
  count ! "get"

  def receive = {
    case count =>
      println(s"count is $count")
      context.stop(self)
  }
}
