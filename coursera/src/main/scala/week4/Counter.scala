package week4

import akka.actor.Actor

/**
  * Created by weili on 16-1-13.
  */
class Counter extends Actor {
  def count(n: Int): Receive = {
    case "inc" => context.become(count(n + 1))
    case "get" => sender ! n
  }

  def receive = count(0)
}
